/*
 * Copyright (C) 2012-2014  Cedric Cheneau
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

package net.holmes.core.backend.response;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Streaming browse result
 */
public class BrowseResult {
    private String parentNodeId;
    private String errorMessage;
    private List<BrowseFolder> folders = Lists.newArrayList();
    private List<BrowseContent> contents = Lists.newArrayList();

    /**
     * Instantiates a new browse result
     */
    public BrowseResult() {
    }

    public String getParentNodeId() {
        return parentNodeId;
    }

    public void setParentNodeId(String parentNodeId) {
        this.parentNodeId = parentNodeId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<BrowseFolder> getFolders() {
        return folders;
    }

    public void setFolders(List<BrowseFolder> folders) {
        this.folders = folders;
    }

    public List<BrowseContent> getContents() {
        return contents;
    }

    public void setContents(List<BrowseContent> contents) {
        this.contents = contents;
    }

    /**
     * Browse folder.
     */
    public static class BrowseFolder {
        private String nodeId;
        private String folderName;

        /**
         * Instantiates a new browse folder.
         */
        public BrowseFolder() {
        }

        /**
         * Instantiates a new browse folder.
         *
         * @param nodeId     node id
         * @param folderName folder name
         */
        public BrowseFolder(final String nodeId, final String folderName) {
            this.nodeId = nodeId;
            this.folderName = folderName;
        }

        public String getNodeId() {
            return nodeId;
        }

        public void setNodeId(String nodeId) {
            this.nodeId = nodeId;
        }

        public String getFolderName() {
            return folderName;
        }

        public void setFolderName(String folderName) {
            this.folderName = folderName;
        }
    }

    /**
     * Browse content.
     */
    public static class BrowseContent {
        private String nodeId;
        private String contentName;
        private String contentUrl;

        /**
         * Instantiates a new browse content.
         */
        public BrowseContent() {
        }

        /**
         * Instantiates a new browse content.
         *
         * @param nodeId      node id
         * @param contentName content name
         * @param contentUrl  content url
         */
        public BrowseContent(final String nodeId, final String contentName, final String contentUrl) {
            this.nodeId = nodeId;
            this.contentName = contentName;
            this.contentUrl = contentUrl;
        }

        public String getNodeId() {
            return nodeId;
        }

        public void setNodeId(String nodeId) {
            this.nodeId = nodeId;
        }

        public String getContentName() {
            return contentName;
        }

        public void setContentName(String contentName) {
            this.contentName = contentName;
        }

        public String getContentUrl() {
            return contentUrl;
        }

        public void setContentUrl(String contentUrl) {
            this.contentUrl = contentUrl;
        }
    }
}
