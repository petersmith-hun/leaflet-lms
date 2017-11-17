package hu.psprog.leaflet.lms.web.menu.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

/**
 * Menu item domain object.
 * Should contain the specification of a menu item.
 * A menu item consists of:
 *  - name: display name of the menu item
 *  - link: URL pointing to a resource (can be absolute and relative as well)
 *  - iconClass: optional icon class name for a (top level) menu item (currently used by the template)
 *  - children: optional children menu items (if any, currently link will be ignored)
 *
 * @author Peter Smith
 */
@JsonDeserialize(builder = MenuItem.MenuItemBuilder.class)
public class MenuItem {

    private String name;
    private String link;
    private String iconClass;
    private List<MenuItem> children;

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    public String getIconClass() {
        return iconClass;
    }

    public List<MenuItem> getChildren() {
        return children;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        MenuItem menuItem = (MenuItem) o;

        return new EqualsBuilder()
                .append(name, menuItem.name)
                .append(link, menuItem.link)
                .append(iconClass, menuItem.iconClass)
                .append(children, menuItem.children)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(link)
                .append(iconClass)
                .append(children)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("link", link)
                .append("iconClass", iconClass)
                .append("children", children)
                .toString();
    }

    public static MenuItemBuilder getBuilder() {
        return new MenuItemBuilder();
    }

    /**
     * Builder for {@link MenuItem}.
     */
    public static final class MenuItemBuilder {
        private String name;
        private String link;
        private String iconClass;
        private List<MenuItem> children;

        private MenuItemBuilder() {
        }

        public MenuItemBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public MenuItemBuilder withLink(String link) {
            this.link = link;
            return this;
        }

        public MenuItemBuilder withIconClass(String iconClass) {
            this.iconClass = iconClass;
            return this;
        }

        public MenuItemBuilder withChildren(List<MenuItem> children) {
            this.children = children;
            return this;
        }

        public MenuItem build() {
            MenuItem menuItem = new MenuItem();
            menuItem.name = this.name;
            menuItem.iconClass = this.iconClass;
            menuItem.link = this.link;
            menuItem.children = this.children;
            return menuItem;
        }
    }
}
