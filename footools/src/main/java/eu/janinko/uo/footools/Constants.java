/*
 * Copyright (C) 2018 Honza Brázdil &lt;jbrazdil@redhat.com&gt;
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
package eu.janinko.uo.footools;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author Honza Brázdil &lt;jbrazdil@redhat.com&gt;
 */
public class Constants {
    private static final Path ULTIMA = Paths.get("/home/janinko/Ultima");
    private static final Path SCRIPTS = ULTIMA.resolve("andaria-git/sphere");
    private static final Path HRA = ULTIMA.resolve("hra");

}
