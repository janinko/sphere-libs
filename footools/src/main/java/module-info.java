/*
 * Copyright (C) 2023 janinko
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

module eu.janinko.uo.footool {
    requires static lombok;
    requires eu.janinko.uo.util;
    requires eu.janinko.uo.spheresave;
    requires eu.janinko.uo.spherescript;
    requires eu.janinko.andaria.ultimasdk;
    requires java.desktop;
    requires org.apache.commons.csv;
    requires org.apache.commons.io;
    requires org.apache.commons.text;
    
    exports eu.janinko.uo.tools;
    exports eu.janinko.uo.tools.generator;
    exports eu.janinko.uo.tools.save;
}
