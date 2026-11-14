/*
 * Copyright (C) 2021 janinko
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

import java.util.Random;

/**
 *
 * @author janinko
 */
public class RandomSimulator {

    static Random generator = new Random();

    public static void main(String[] args) {

        for (int count = 1; count < 20; count++) {
            //int count = 10;
            int tries = 1000000;
            int a = 0;
            int b = 0;
            int c = 0;

            for (int i = 0; i < tries; i++) {
                if (randomSelection(count)) {
                    a++;
                }
                if (oneColorOnly(count)) {
                    b++;
                }
                if (oneColorOrRandom(count)) {
                    c++;
                }
            }

            System.out.println("count: " + count);
            System.out.printf("randomSelection  %d / %d = %.2f %%%n", a, tries, (double) a / tries * 100.0);
            System.out.printf("oneColorOnly     %d / %d = %.2f %%%n", b, tries, (double) b / tries * 100.0);
            System.out.printf("oneColorOrRandom %d / %d = %.2f %%%n", c, tries, (double) c / tries * 100.0);
            System.out.println();
        }
    }

    public static boolean randomSelection(int count) {
        int viteznaPozice = generator.nextInt(count);
        int vybranaPozice = generator.nextInt(count);
        return viteznaPozice == vybranaPozice;
    }

    public static boolean oneColorOnly(int count) {
        int[] options;
        OUT:
        while (true) {
            options = getOptions(count);
            for (int i = 0; i < count; i++) {
                if (options[i] == 0) {
                    break OUT;
                }
            }
        }
        int viteznaPozice = generator.nextInt(count);
        int vybranaPozice = selectFromArray(options);
        return viteznaPozice == vybranaPozice;
    }

    public static boolean oneColorOrRandom(int count) {
        int[] options = getOptions(count);
        int viteznaPozice = generator.nextInt(count);
        int vybranaPozice = selectFromArray(options);
        if (vybranaPozice == -1) {
            vybranaPozice = generator.nextInt(count);
        }
        return viteznaPozice == vybranaPozice;
    }

    public static int selectFromArray(int[] options) {
        int count = 0;
        for (int i = 0; i < options.length; i++) {
            if (options[i] == 0) {
                count++;
            }
        }
        if (count == 0) {
            return -1;
        }
        int vyber = generator.nextInt(count);
        count = 0;
        for (int i = 0; i < options.length; i++) {
            if (options[i] == 0) {
                if (count++ == vyber) {
                    return i;
                }
            }
        }
        throw new IllegalStateException();
    }

    public static int[] getOptions(int count) {
        int[] ret = new int[count];
        for (int i = 0; i < count; i++) {
            ret[i] = generator.nextInt(3);
        }
        return ret;
    }

}
