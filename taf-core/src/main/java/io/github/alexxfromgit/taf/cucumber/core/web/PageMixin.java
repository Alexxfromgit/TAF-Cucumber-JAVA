package io.github.alexxfromgit.taf.cucumber.core.web;

/**
 * Base for mixin interfaces that share UI between pages without inheritance chains:
 * <pre>{@code
 * public interface HasHeader extends PageMixin {
 *     default Header header() { return component(Header.class); }
 * }
 * public class InventoryPage extends Page implements HasHeader { ... }
 * }</pre>
 */
public interface PageMixin {

    <C extends Component> C component(Class<C> type);
}
