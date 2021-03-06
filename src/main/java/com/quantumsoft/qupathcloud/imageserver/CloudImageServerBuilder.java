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

package com.quantumsoft.qupathcloud.imageserver;

import static com.quantumsoft.qupathcloud.configuration.MetadataConfiguration.METADATA_FILE_EXTENSION;

import com.quantumsoft.qupathcloud.dao.CloudDao;
import com.quantumsoft.qupathcloud.exception.QuPathCloudException;
import com.quantumsoft.qupathcloud.repository.Repository;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FilenameUtils;
import qupath.lib.images.servers.FileFormatInfo;
import qupath.lib.images.servers.ImageServer;
import qupath.lib.images.servers.ImageServerBuilder;

/**
 * Cloud image server builder for creating CloudImageServer.
 */
public class CloudImageServerBuilder implements ImageServerBuilder<BufferedImage> {

  private final CloudDao cloudDao;

  /**
   * Instantiates a new Cloud image server builder.
   */
  public CloudImageServerBuilder() {
    cloudDao = Repository.INSTANCE.getCloudDao();
  }

  @Override
  public float supportLevel(URI uri, FileFormatInfo.ImageCheckType info, Class<?> cls) {
    Path filePath = Paths.get(uri);
    String extension = FilenameUtils.getExtension(filePath.toString());
    if (extension.equals(METADATA_FILE_EXTENSION)) {
      return 10;
    } else {
      return 0;
    }
  }

  @Override
  public ImageServer<BufferedImage> buildServer(URI uri) throws QuPathCloudException {
    return new CloudImageServer(uri, cloudDao, Repository.INSTANCE.getDicomStore());
  }

  @Override
  public String getName() {
    return "CloudImageServer Builder";
  }

  @Override
  public String getDescription() {
    return "Pyramid image with tiles obtained via google healthcare api";
  }
}
