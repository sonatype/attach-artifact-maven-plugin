package org.codehaus.mojo.buildhelper;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.io.File;

/**
 * @author dtran
 */
public class AttachArtifact
{
    private String artifactId;

    private String classifier;

    private File file;

    private String groupId;

    private File pomFile;

    private String type;

    private Boolean updatePlugin;

    private Boolean updatePom;

    private String version;

    private String goalPrefix;

    public String getGoalPrefix()
    {
        return goalPrefix;
    }

    public void setGoalPrefix( String goalPrefix )
    {
        this.goalPrefix = goalPrefix;
    }

    public String getArtifactId()
    {
        return artifactId;
    }

    public String getClassifier()
    {
        return this.classifier;
    }

    public File getFile()
    {
        return this.file;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public File getPomFile()
    {
        return pomFile;
    }

    public String getType()
    {
        return this.type;
    }

    public String getVersion()
    {
        return version;
    }

    public Boolean isUpdatePlugin()
    {
        return updatePlugin;
    }

    public Boolean isUpdatePom()
    {
        return updatePom;
    }

    public void setArtifactId( String artifactId )
    {
        this.artifactId = artifactId;
    }

    public void setClassifier( String classifier )
    {
        this.classifier = classifier;
    }

    public void setFile( File localFile )
    {
        this.file = localFile;
    }

    public void setGroupId( String groupId )
    {
        this.groupId = groupId;
    }

    public void setPomFile( File pomFile )
    {
        this.pomFile = pomFile;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public void setUpdatePlugin( Boolean updatePlugin )
    {
        this.updatePlugin = updatePlugin;
    }

    public void setUpdatePom( Boolean updatePom )
    {
        this.updatePom = updatePom;
    }

    public void setVersion( String version )
    {
        this.version = version;
    }

    @Override
    public String toString()
    {
        return ( groupId != null ? groupId + ":" : "" ) + artifactId + ":" + ( version != null ? version + ":" : "" )
            + type;
    }
}
