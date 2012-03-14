#
# Just a bunch of crap - Old build.xml file actually
# I just wanted to see if the parser freaks out real bad when I give it an obviously bad file
#
<project name="sleep" default="all" basedir=".">
  <property name="project.src"   location="src" />
  <property name="project.build" location="bin" />

  <property name="project.path"  value="sleep" />

  <target name="all" depends="init, compile" />

  <target name="init">
    <tstamp />
    <mkdir dir="${project.build}" />
  </target>

  <target name="compile-nodeps" description="compile the source " >
    <javac srcdir="${project.src}/${project.path}" 
           destdir="${project.build}"
           nowarn="yes"
           depend="yes"

           debug="yes"
    />
  </target>


  <target name="compile" depends="init,interfaces,runtime,atoms,engine,parser" description="compile the source " >
    <javac srcdir="${project.src}/${project.path}" 
           destdir="${project.build}"
           nowarn="yes"
           depend="yes"
    />
  </target>

  <target name="atoms" description="compiles the atoms">
    <javac srcdir="${project.src}/${project.path}/engine/atoms" 
           destdir="${project.build}"
           nowarn="yes"
           depend="yes"
    />
  </target>

  <target name="engine" description="compiles the atoms">
    <javac srcdir="${project.src}/${project.path}/engine" 
           destdir="${project.build}"
           nowarn="yes"
           depend="yes"
    />
  </target>

  <target name="parser" description="compiles the atoms">
    <javac srcdir="${project.src}/${project.path}/parser" 
           destdir="${project.build}"
           nowarn="yes"
           depend="yes"
    />
  </target>

  <target name="runtime" description="compiles the atoms">
    <javac srcdir="${project.src}/${project.path}/runtime" 
           destdir="${project.build}"
           nowarn="yes"
           depend="yes"
    />
  </target>

  <target name="interfaces" description="compiles the atoms">
    <javac srcdir="${project.src}/${project.path}/interfaces" 
           destdir="${project.build}"
           nowarn="yes"
           depend="yes"
    />
  </target>

  <target name="docs" description="build java documentation">
     <javadoc packagenames="sleep.*"
              sourcepath="src"
              destdir="docs/api"
              windowtitle="Sleep API" />
  </target>

  <target name="jar" depends="compile-nodeps">
     <jar destfile="../sleep.jar"
         basedir="bin"
         includes="**/*.class"
         />
  </target>

  <target name="clean" description="clean up" >
    <delete dir="${build}"/>
  </target>
</project>

