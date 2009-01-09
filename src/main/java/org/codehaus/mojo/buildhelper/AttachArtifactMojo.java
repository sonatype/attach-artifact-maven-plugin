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
import java.io.FileReader;
import java.io.FileWriter;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.zip.ZipArchiver;
import org.codehaus.plexus.archiver.zip.ZipUnArchiver;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.Xpp3DomWriter;

/**
 * Attach additional artifacts to be installed and deployed.
 * 
 * @goal attach-artifact
 * @phase package
 * @author <a href="dantran@gmail.com">Dan T. Tran</a>
 * @author <a href="velo.br@gmail.com">Marvin H. Froeder</a>
 * @version $Id$
 * @since 1.0
 */
public class AttachArtifactMojo
    extends AbstractMojo
{
    /**
     * Attach an array of artifacts to the project.
     * 
     * @parameter
     * @required
     */
    private AttachArtifact[] artifacts;

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @component
     * @required
     * @readonly
     */
    private ArtifactFactory artifactFactory;

    /**
     * @component role="org.codehaus.plexus.archiver.Archiver" roleHint="zip"
     * @required
     * @readonly
     */
    private ZipArchiver zipArchiver;

    /**
     * @component role="org.codehaus.plexus.archiver.UnArchiver" roleHint="zip"
     * @required
     * @readonly
     */
    private ZipUnArchiver zipUnArchiver;

    /**
     * @parameter default-value="${project.build.directory}/attach-artifact-plugin"
     * @required
     * @readonly
     */
    private File output;

    /**
     * @parameter
     */
    private File defaultPomFile;

    /**
     * @parameter
     */
    private File defaultFile;

    /**
     * @parameter
     */
    private boolean updatePom;

    /**
     * @parameter
     */
    private boolean updatePlugin;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        for ( AttachArtifact attArtifact : artifacts )
        {
            try
            {
                attachArtifact( attArtifact );
            }
            catch ( Exception e )
            {
                throw new MojoFailureException( "Error attaching " + attArtifact, e );
            }
        }
        // for ( int i = 0; i < this.artifacts.length; ++i )
        // {
        // projectHelper.attachArtifact( this.project, this.artifacts[i].getType(), this.artifacts[i].getClassifier(),
        // this.artifacts[i].getFile() );
        // }

    }

    private void attachArtifact( AttachArtifact attArtifact )
        throws Exception
    {
        String groupId = attArtifact.getGroupId() != null ? attArtifact.getGroupId() : project.getGroupId();
        String artifactId = attArtifact.getArtifactId();
        String version = attArtifact.getVersion() != null ? attArtifact.getVersion() : project.getVersion();
        String type = attArtifact.getType() != null ? attArtifact.getType() : "jar";
        String classifier = attArtifact.getClassifier();
        File attFile = attArtifact.getFile() != null ? attArtifact.getFile() : defaultFile;
        File attPomFile = attArtifact.getPomFile() != null ? attArtifact.getPomFile() : defaultPomFile;

        boolean updatePom = attArtifact.isUpdatePom() != null ? attArtifact.isUpdatePom() : this.updatePom;
        boolean updatePlugin = attArtifact.isUpdatePlugin() != null ? attArtifact.isUpdatePlugin() : this.updatePlugin;
        Artifact artifact =
            artifactFactory.createArtifactWithClassifier( groupId, artifactId, version, type, classifier );
        File file;
        if ( updatePlugin )
        {
            String goalPrefix =
                attArtifact.getGoalPrefix() != null ? attArtifact.getGoalPrefix() : attArtifact.getArtifactId();
            file = updatePlugin( attFile, groupId, artifactId, version, goalPrefix );
        }
        else
        {
            file = attFile;
        }
        artifact.setFile( file );
        artifact.setResolved( true );

        project.addAttachedArtifact( artifact );

        if ( attPomFile != null )
        {
            Artifact pomArtifact =
                artifactFactory.createArtifactWithClassifier( groupId, artifactId, version, "pom", classifier );

            File pomFile;
            if ( updatePom )
            {
                pomFile = updatePom( attPomFile, groupId, artifactId, version, type );
            }
            else
            {
                pomFile = attPomFile;
            }

            pomArtifact.setFile( pomFile );
            pomArtifact.setResolved( true );

            project.addAttachedArtifact( pomArtifact );
        }
    }

    private File updatePom( File originalPomFile, String groupId, String artifactId, String version, String type )
        throws Exception
    {
        File pomFile = new File( output, artifactId + ".pom" );

        Xpp3Dom dom;
        FileReader reader = new FileReader( originalPomFile );
        try
        {
            dom = Xpp3DomBuilder.build( reader );
            Xpp3Dom projectDom = dom;
            updateValue( projectDom, "groupId", groupId );
            updateValue( projectDom, "artifactId", artifactId );
            updateValue( projectDom, "version", version );
            updateValue( projectDom, "packaging", version );

        }
        finally
        {
            IOUtil.close( reader );
        }

        FileWriter writer = new FileWriter( pomFile );
        try
        {
            Xpp3DomWriter.write( writer, dom );
        }
        finally
        {
            IOUtil.close( writer );
        }
        return pomFile;
    }

    private void updateValue( Xpp3Dom dom, String childName, String value )
    {
        Xpp3Dom child = dom.getChild( childName );
        if ( child != null )
        {
            child.setValue( value );
        }
    }

    private File updatePlugin( File originalFile, String groupId, String artifactId, String version, String goalPrefix )
        throws Exception
    {
        zipArchiver.reset();
        // zipUnArchiver.reset();

        File output = new File( this.output, artifactId );
        output.mkdirs();

        zipUnArchiver.setSourceFile( originalFile );
        zipUnArchiver.setDestDirectory( output );
        zipUnArchiver.extract();

        File pluginFile = new File( output, "META-INF/maven/plugin.xml" );

        FileReader reader = new FileReader( pluginFile );
        Xpp3Dom dom;
        try
        {
            dom = Xpp3DomBuilder.build( reader );
            updateValue( dom, "groupId", groupId );
            updateValue( dom, "artifactId", artifactId );
            updateValue( dom, "version", version );
            updateValue( dom, "goalPrefix", goalPrefix );

        }
        finally
        {
            IOUtil.close( reader );
        }

        FileWriter writer = new FileWriter( pluginFile );
        try
        {
            Xpp3DomWriter.write( writer, dom );
        }
        finally
        {
            IOUtil.close( writer );
        }

        File file = new File( this.output, artifactId + ".jar" );
        zipArchiver.addDirectory( output );
        zipArchiver.setDestFile( file );
        zipArchiver.createArchive();
        return file;
    }
}
