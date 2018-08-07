/*
    Copyright 2016-2018 Will Winder

    This file is part of Universal Gcode Sender (UGS).

    UGS is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    UGS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with UGS.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.willwinder.universalgcodesender.gcode.util;

import com.willwinder.universalgcodesender.Utils;
import com.willwinder.universalgcodesender.model.UnitUtils;
import com.willwinder.universalgcodesender.model.UnitUtils.Units;

/**
 *
 * @author wwinder
 */
public class GcodeUtils {
    public static String unitCommand(Units units) {
      // Change units.
      switch(units) {
        case MM:
          return "G21";
        case INCH:
          return "G20";
        default:
          return "";
      }
    }

    /**
     * Generates a jog command given a base command. The command will be appended with the relative movement to be made
     * for zero to all axises with the given distance and feed rate. This method will add the command to switch units
     * to the given units. Remember to switch back to previous units after command finishes.
     *
     * @param command the base command to use, ie: G91G1
     * @param units the units that the distance and feed rate are given in. The unit such as "G21" or "G20" will be prepended before the jog command
     * @param distance the distance to move
     * @param dirX 1 for positive movement, 0 for no movement, -1 for negative movement
     * @param dirY 1 for positive movement, 0 for no movement, -1 for negative movement
     * @param dirZ 1 for positive movement, 0 for no movement, -1 for negative movement
     */
    public static String generateJogCommand(String command, Units units,
                                            double distance, double feedRate, int dirX, int dirY, int dirZ) {
        return generateJogCommand(unitCommand(units) + command, units, distance, feedRate, dirX, dirY, dirZ, units);
    }

    /**
     * Generates a jog command given a base command. The command will be appended with the relative movement to be made
     * for zero to all axises with the given distance and feed rate. This method will generate the command and convert the
     * distance and feed rate to the given target units.
     *
     * Ex. If the given distance is 1 inch and the target units is millimeters, the distance will be converted to
     * 25.4mm.
     *
     * @param command the base command to use, ie: G91G1
     * @param units the units that the distance and feed rate are given in
     * @param distance the distance to move
     * @param feedRate the feed rate to move with
     * @param dirX 1 for positive movement, 0 for no movement, -1 for negative movement
     * @param dirY 1 for positive movement, 0 for no movement, -1 for negative movement
     * @param dirZ 1 for positive movement, 0 for no movement, -1 for negative movement
     * @param targetUnits what target units should the command be converted to. Should be the current gcode state (G20 or G21)
     * @return a string with the complete jog command
     */
    public static String generateJogCommand(String command, Units units, double distance, double feedRate, int dirX, int dirY, int dirZ, Units targetUnits) {
        StringBuilder sb = new StringBuilder();

        // Scale the feed rate and distance to the current coordinate units
        double scale = UnitUtils.scaleUnits(units, targetUnits);
        String convertedDistance = Utils.formatter.format(distance * scale);
        String convertedFeedRate = Utils.formatter.format(feedRate * scale);

        // Set command.
        sb.append(command);

        if (dirX != 0) {
            sb.append("X");
            if (dirX < 0) {
                sb.append("-");
            }
            sb.append(convertedDistance);
        }

        if (dirY != 0) {
            sb.append("Y");
            if (dirY < 0) {
                sb.append("-");
            }
            sb.append(convertedDistance);
        }

        if (dirZ != 0) {
            sb.append("Z");
            if (dirZ < 0) {
                sb.append("-");
            }
            sb.append(convertedDistance);
        }

        if (convertedFeedRate != null) {
            sb.append("F").append(convertedFeedRate);
        }

        return sb.toString();
    }
}
