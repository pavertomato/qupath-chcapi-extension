// Copyright (C) 2019 Google LLC
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.quantumsoft.qupathcloud.pyramid;

import com.quantumsoft.qupathcloud.entities.DicomAttribute;
import com.quantumsoft.qupathcloud.entities.instance.Instance;
import com.quantumsoft.qupathcloud.entities.instance.objects.ObjectOfPerframeFunctionalGroupsSequence;
import com.quantumsoft.qupathcloud.exception.QuPathCloudException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PyramidLevel {
    private static final Logger LOGGER = LogManager.getLogger();

    private int width;
    private int height;
    private int tileWidth;
    private int tileHeight;

    private boolean isFullTiled;

    private HashMap<Point, PyramidFrame> frameMap = new HashMap<>();

    public PyramidLevel(Instance instance) throws QuPathCloudException {
        this.width = instance.getTotalPixelMatrixColumns().getValue1();
        this.height = instance.getTotalPixelMatrixRows().getValue1();
        this.tileWidth = instance.getColumns().getValue1();
        this.tileHeight = instance.getRows().getValue1();
        this.isFullTiled = instance.isFullTiled();

        addInstance(instance);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public PyramidFrame getFrame(int tileX, int tileY) {
        return frameMap.get(new Point(tileX, tileY));
    }

    public void addInstance(Instance instance) throws QuPathCloudException {
        if (instance.getTotalPixelMatrixColumns().getValue1() != width ||
                instance.getTotalPixelMatrixRows().getValue1() != height ||
                instance.getColumns().getValue1() != tileWidth ||
                instance.getRows().getValue1() != tileHeight ||
                instance.isFullTiled() != isFullTiled
        ) {
            throw new QuPathCloudException("PyramidLevel and instance parameters do not match");
        }
        String instanceUID = instance.getSopInstanceUID().getValue1();

        if (isFullTiled) {
            int numberOfFrames = instance.getNumberOfFrames().getValue1();
            int frameOffset = instance.getConcatenationFrameOffsetNumber().getValue1();

            int widthInTiles = (int) Math.ceil((double) width / tileWidth);
            int heightInTiles = (int) Math.ceil((double) height / tileHeight);

            int startX = Math.floorMod(frameOffset, widthInTiles);
            int startY = Math.floorDiv(frameOffset, widthInTiles);

            int frameIndex = 0;
            for (int y = startY; y < heightInTiles; y++) {
                for (int x = frameIndex == 0 ? startX : 0; x < widthInTiles; x++) {
                    Point tileCoordinate = new Point(x + 1, y + 1);
                    if (frameMap.get(tileCoordinate) != null) {
                        throw new QuPathCloudException("PyramidLevel build error: tiles with same coordinates");
                    }
                    PyramidFrame frame = new PyramidFrame(instanceUID, ++frameIndex);
                    frameMap.put(tileCoordinate, frame);

                    if (frameIndex >= numberOfFrames) break;
                }

                if (frameIndex >= numberOfFrames) break;
            }
        } else {
            ObjectOfPerframeFunctionalGroupsSequence[] objectsOfPerframeFunctionalGroupsSequences =
                    instance.getPerframeFunctionalGroupsSequence().getValue();
            List<ObjectOfPerframeFunctionalGroupsSequence> frameList =
                    Arrays.asList(objectsOfPerframeFunctionalGroupsSequences);
            for (int i = 0; i < frameList.size(); i++) {
                Point tileCoordinate = getFrameCoordinate(frameList.get(i));
                if (frameMap.get(tileCoordinate) != null) {
                    throw new QuPathCloudException("PyramidLevel build error: tiles with same coordinates");
                }
                PyramidFrame frame = new PyramidFrame(instanceUID, i + 1);
                frameMap.put(tileCoordinate, frame);
            }
        }
    }

    private static Point getFrameCoordinate(ObjectOfPerframeFunctionalGroupsSequence frame) {
        DicomAttribute<Integer> values = frame.getFrameContentSequence().getValue1().getDimensionIndexValues();
        return new Point(values.getValue1(), values.getValue2());
    }
}
