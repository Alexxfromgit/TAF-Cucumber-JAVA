package io.github.alexxfromgit.taf.cucumber.core.web;

import io.github.alexxfromgit.taf.cucumber.core.config.TafConfig;
import io.github.alexxfromgit.taf.cucumber.core.failure.FrameworkException;
import io.github.alexxfromgit.taf.cucumber.core.log.Log;
import io.qameta.allure.model.Status;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Supplier;

/**
 * Creates pages and components and injects their {@link Locate}-annotated fields: {@link UiElement},
 * {@link UiElements}, {@link Component} subclasses and {@link ComponentList}. Creation is cheap (nothing is looked up
 * until used), so create pages freely.
 */
public final class PageFactory {

    private PageFactory() {
    }

    /** Navigates to the page's {@link Url} (relative to {@code web.base-url}) and waits until it is ready. */
    public static <P extends Page> P open(Class<P> type) {
        Url url = type.getAnnotation(Url.class);
        if (url == null) {
            throw new FrameworkException(type.getSimpleName() + " has no @Url, so it cannot be opened directly");
        }
        String target = url.value().startsWith("http") ? url.value()
                : TafConfig.get().string("web.base-url").replaceAll("/$", "") + url.value();
        Log.step("Open " + target, Status.PASSED);
        DriverManager.driver().get(target);
        return create(type).waitReady();
    }

    /** The page the browser shows now, after waiting until it is ready: {@code ready(CartPage.class).checkout()}. */
    public static <P extends Page> P ready(Class<P> type) {
        return create(type).waitReady();
    }

    /** A page bound to the current thread's browser (not waited for: use {@link #ready(Class)} for that). */
    public static <P extends Page> P create(Class<P> type) {
        return create(type, DriverManager::driver);
    }

    /** A page bound to an explicit search context (useful in framework unit tests). */
    public static <P extends Page> P create(Class<P> type, Supplier<? extends SearchContext> context) {
        P page = instantiate(type);
        page.init(type.getSimpleName(), context);
        inject(page, context);
        return page;
    }

    static <C extends Component> C component(Class<C> type, UiContainer parent) {
        Locate locate = type.getAnnotation(Locate.class);
        if (locate == null) {
            throw new FrameworkException(type.getSimpleName() + " needs a class-level @Locate to be used with "
                    + "component(...). Alternatively declare it as a @Locate-annotated field.");
        }
        String field = Character.toLowerCase(type.getSimpleName().charAt(0)) + type.getSimpleName().substring(1);
        By by = LocatorResolver.resolve(locate, parent.name() + "." + field);
        return componentAt(type, new UiElement(parent.name(), field, by, parent.searchContext()));
    }

    static <C extends Component> C componentAt(Class<C> type, UiElement root) {
        C component = instantiate(type);
        Supplier<SearchContext> context = root::waitVisible;
        component.init(root.name(), context);
        component.root(root);
        inject(component, context);
        return component;
    }

    private static void inject(UiContainer container, Supplier<? extends SearchContext> context) {
        for (Class<?> type = container.getClass();
             type != Page.class && type != Component.class && type != UiContainer.class && type != Object.class;
             type = type.getSuperclass()) {
            for (Field field : type.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || !isInjectable(field.getType())) {
                    continue;
                }
                set(container, field, valueFor(container, field, context));
            }
        }
    }

    private static Object valueFor(UiContainer container, Field field, Supplier<? extends SearchContext> context) {
        String where = container.name() + "." + field.getName();
        Class<?> fieldType = field.getType();
        Locate locate = field.getAnnotation(Locate.class);
        if (locate == null && Component.class.isAssignableFrom(fieldType)) {
            locate = fieldType.getAnnotation(Locate.class);
        }
        if (locate == null) {
            throw new FrameworkException(where + " needs @Locate");
        }
        UiElement element = new UiElement(container.name(), field.getName(), LocatorResolver.resolve(locate, where),
                context);
        if (fieldType == UiElement.class) {
            return element;
        }
        if (fieldType == UiElements.class) {
            return new UiElements(element);
        }
        if (fieldType == ComponentList.class) {
            return new ComponentList<>(itemType(field, where), element);
        }
        return componentAt(fieldType.asSubclass(Component.class), element);
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends Component> itemType(Field field, String where) {
        Type generic = field.getGenericType();
        if (generic instanceof ParameterizedType parameterized
                && parameterized.getActualTypeArguments()[0] instanceof Class<?> item
                && Component.class.isAssignableFrom(item)) {
            return (Class<? extends Component>) item;
        }
        throw new FrameworkException(where + " must be declared as ComponentList<SomeComponent>");
    }

    private static boolean isInjectable(Class<?> type) {
        return type == UiElement.class || type == UiElements.class || type == ComponentList.class
                || Component.class.isAssignableFrom(type);
    }

    private static <T> T instantiate(Class<T> type) {
        try {
            Constructor<T> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new FrameworkException(type.getSimpleName() + " needs a no-argument constructor", e);
        } catch (ReflectiveOperationException e) {
            throw new FrameworkException("Cannot create " + type.getSimpleName(), e);
        }
    }

    private static void set(Object target, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw new FrameworkException("Cannot inject " + field, e);
        }
    }
}
