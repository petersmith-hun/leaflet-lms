package hu.psprog.leaflet.lms.web.menu.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

/**
 * System menu domain object.
 * Should contain a list of top level {@link MenuItem} instances.
 *
 * @author Peter Smith
 */
@JsonDeserialize(builder = SystemMenu.SystemMenuBuilder.class)
public class SystemMenu {

    private List<MenuItem> menu;

    public List<MenuItem> getMenu() {
        return menu;
    }

    public static SystemMenuBuilder getBuilder() {
        return new SystemMenuBuilder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SystemMenu menu1 = (SystemMenu) o;

        return new EqualsBuilder()
                .append(menu, menu1.menu)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(menu)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("menu", menu)
                .toString();
    }

    /**
     * Builder for {@link SystemMenu}.
     */
    public static final class SystemMenuBuilder {
        private List<MenuItem> menu;

        private SystemMenuBuilder() {
        }

        public SystemMenuBuilder withMenu(List<MenuItem> menu) {
            this.menu = menu;
            return this;
        }

        public SystemMenu build() {
            SystemMenu systemMenu = new SystemMenu();
            systemMenu.menu = this.menu;
            return systemMenu;
        }
    }
}
