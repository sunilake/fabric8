/**
 *  Copyright 2005-2016 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package io.fabric8.api;

import java.util.List;
import java.util.Map;


/**
 * The public profile manager
 */
public interface ProfileManager {

    Version createVersion(Version version);
    
    Version createVersionFrom(String sourceId, String targetId, Map<String, String> attributes);
    
    List<String> getVersions();
    
    boolean hasVersion(String versionId);
    
    Version getVersion(String versionId);
    
    Version getRequiredVersion(String versionId);

    // [TODO] Add ProfileService.updateVersion(Version version)
    //Version updateVersion(Version version);

    void deleteVersion(String versionId);
    
    Profile createProfile(Profile profile);
    
    boolean hasProfile(String versionId, String profileId);
    
    Profile getProfile(String versionId, String profileId);
    
    Profile getRequiredProfile(String versionId, String profileId);
    
    Profile getOverlayProfile(Profile profile);

    Profile updateProfile(Profile profile);

    /**
     * Always update profile. Used to modify resource, as equality of profiles doesn't take into account resources contents)
     * @param profile
     * @param force
     * @return
     */
    Profile updateProfile(Profile profile, boolean force);

    void deleteProfile(String versionId, String profileId, boolean force);
}
