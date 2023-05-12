/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

import org.gradle.api.*
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

import javax.inject.Inject

abstract class Extension {
    protected Debug debug
    protected Release release
    protected Lint lint
    protected Configuration configuration
    protected Version version
    protected Build build
    private Action<PluginTezovApp.BuildType> actionBeforeVariant = null
    private Action<PluginTezovApp.BuildType> actionWhenEvaluated = null
    private Action<PluginTezovApp.BuildType> actionWhenReady = null

    @Inject
    abstract ObjectFactory getObjectFactory()

    Extension() {
        initDebug()
        initRelease()
        initLint()
        initConfiguration()
        version = getObjectFactory().newInstance(Version)
        build = getObjectFactory().newInstance(Build)

    }

    void debug(Closure action) {
        action.delegate = debug
        action.resolveStrategy = Closure.DELEGATE_ONLY
        action.call()
    }
    void release(Closure action) {
        action.delegate = release
        action.resolveStrategy = Closure.DELEGATE_ONLY
        action.call()
    }
    void version(Closure action) {
        action.delegate = version
        action.resolveStrategy = Closure.DELEGATE_ONLY
        action.call()
    }
    void configuration(Closure action) {
        action.delegate = configuration
        action.resolveStrategy = Closure.DELEGATE_ONLY
        action.call()
    }

    protected void initDebug() {
        debug = getObjectFactory().newInstance(Debug)
        debug.enable.set(false)
        debug.keepLog.set(true)
        debug.keepSourceFile.set(true)
        debug.repackage.set(false)
        debug.obfuscate.set(false)
    }
    protected void initRelease() {
        release = getObjectFactory().newInstance(Release)
        release.obfuscate.set(true)
    }
    protected void initLint() {
        lint = getObjectFactory().newInstance(Lint)
        lint.abortOnError.set(false)
        lint.checkReleaseBuilds.set(false)
    }
    protected void initConfiguration() {
        configuration = getObjectFactory().newInstance(Configuration)
        configuration.library.set(false)
    }
    protected void initVersionName() {
        def major = version.major.get()
        def minor = version.minor.get()
        def patch = version.patch.get()
        version.name.set("${major}.${minor}.${patch}")
        version.value.set(major * 10000 + minor * 100 + patch)
    }
    protected void initBuildType(List<Task> graphTasks) {
        def taskPreDebugBuild = graphTasks.find { task -> task.name == PluginTezovApp.BuildType.DEBUG.preBuildName() }
        def taskPreReleaseBuild = graphTasks.find { task -> task.name == PluginTezovApp.BuildType.RELEASE.preBuildName() }
        if ((taskPreDebugBuild != null) && (taskPreReleaseBuild == null)) {
            build.type.set(PluginTezovApp.BuildType.DEBUG)
        } else if ((taskPreReleaseBuild != null) && (taskPreDebugBuild == null)) {
            build.type.set(PluginTezovApp.BuildType.RELEASE)
        } else if ((taskPreDebugBuild != null) && (taskPreReleaseBuild != null)) {
            throw new GradleException("Debug and Release task found...")
        } else {
            build.type.set(PluginTezovApp.BuildType.UNKNOWN)
        }
    }

    private void executeAction(Action<PluginTezovApp.BuildType> action) {
        [PluginTezovApp.BuildType.DEBUG, PluginTezovApp.BuildType.RELEASE].each {
            action.execute(it)
        }
    }
    void beforeVariant(Action<PluginTezovApp.BuildType> action) {
        if (actionBeforeVariant != null) {
            throw GradleException("actionBeforeVariant already exist")
        }
        actionBeforeVariant = action
    }
    protected void executeBeforeVariant() {
        if (actionBeforeVariant != null) {
            executeAction(actionBeforeVariant)
        }
    }
    void whenEvaluated(Action<PluginTezovApp.BuildType> action) {
        if (actionWhenEvaluated != null) {
            throw GradleException("actionWhenEvaluated already exist")
        }
        actionWhenEvaluated = action
    }
    protected void executeWhenEvaluated() {
        if (actionWhenEvaluated != null) {
            executeAction(actionWhenEvaluated)
        }
    }
    void whenReady(Action<PluginTezovApp.BuildType> action) {
        if (actionWhenReady != null) {
            throw GradleException("actionWhenReady already exist")
        }
        actionWhenReady = action
    }
    protected void executeWhenReady() {
        if (actionWhenReady != null) {
            executeAction(actionWhenReady)
        }
    }

    interface Debug {
        Property<Boolean> getEnable()

        Property<Boolean> getKeepLog()

        Property<Boolean> getKeepSourceFile()

        Property<Boolean> getRepackage()

        Property<Boolean> getObfuscate()
    }
    interface Release {
        Property<Boolean> getObfuscate()
    }
    interface Lint {
        Property<Boolean> getAbortOnError()
        Property<Boolean> getCheckReleaseBuilds()
    }
    interface Configuration {
        Property<Boolean> getLibrary()
        Property<String> getApplicationId()
        Property<Collection<String>> getProguardPaths()
        Property<Collection<String>> getClassesToImport()
    }
    interface Version {
        Property<Integer> getMajor()

        Property<Integer> getMinor()

        Property<Integer> getPatch()

        Property<String> getName()

        Property<Integer> getValue()
    }
    interface Build {
        Property<PluginTezovApp.BuildType> getType()
    }
}

class PluginTezovApp implements Plugin<Project> {
    enum BuildType {
        UNKNOWN(".unknown"), DEBUG(".dbg"), RELEASE(".rse");
        def extension

        BuildType(extension) {
            this.extension = extension
        }

        def getExtension() {
            return extension
        }

        def getPackageName(Project project) {
            return getApplicationId(project) + extension
        }

        private static def getApplicationId(Project project) {
            return project.extensions.findByName(EXTENSION_NAME).configuration.applicationId.get()
        }

        String capitalName() {
            name().toLowerCase().capitalize()
        }

        String preBuildName() {
            'pre' + capitalName() + 'Build'
        }
    }
    private final static def EXTENSION_NAME = 'tezov'
    private final static def ANDROID_PLUGIN_NAME = 'android'
    private final static def SOURCE_DIR_NAME = 'src'
    @Override
    void apply(Project project) {
        Extension extension = project.extensions.create(EXTENSION_NAME, Extension)
        project.getGradle().afterProject { me ->
            if (me == project) {
                def androidExtension = project.extensions.findByName(ANDROID_PLUGIN_NAME)
                if (extension.configuration.library.get()) {
                    configureAndroidLib(project, androidExtension, extension)
                }
                else {
                    configureAndroidApp(project, androidExtension, extension)
                    configureProguardApp(project, androidExtension, extension)
                }
                tasksProjectRegister(project, extension)
                extension.executeBeforeVariant()
            }
        }
        project.getGradle().projectsEvaluated {
            tasksProjectRegisterDependsOn(project, extension)
            extension.executeWhenEvaluated()
        }
        project.getGradle().getTaskGraph().whenReady {
            extension.initBuildType(project.getGradle().getTaskGraph().allTasks)
            tasksProjectRegisterInput(project, extension)
            extension.executeWhenReady()
        }
    }
    private static void tasksProjectRegister(Project project, Extension extension) {
        project.tasks.register('taskUnCommentDebug', TaskUnCommentDebug) {
            source = new File(project.projectDir, SOURCE_DIR_NAME)
        }
        if(extension.configuration.classesToImport.isPresent() || !extension.configuration.library.get()) {
            project.tasks.register('taskImportClassToAllDebug', TaskImportClassToAllDebug) {
                source = new File(project.projectDir, SOURCE_DIR_NAME)
                if(extension.configuration.classesToImport.isPresent()){
                    classesToImport = extension.configuration.classesToImport.get()
                }
            }
            project.tasks.findByName('taskUnCommentDebug').finalizedBy('taskImportClassToAllDebug')
        }
        project.tasks.register('taskCommentDebug', TaskCommentDebug) {
            source = new File(project.projectDir, SOURCE_DIR_NAME)
        }
    }
    private static void tasksProjectRegisterDependsOn(Project project, Extension extension) {
        def taskPreDebugBuild = project.getTasks().findByName(BuildType.DEBUG.preBuildName())
        if (taskPreDebugBuild != null) {
            taskPreDebugBuild.dependsOn('taskUnCommentDebug')
        }
        def taskPreReleaseBuild = project.getTasks().findByName(BuildType.RELEASE.preBuildName())
        if (taskPreReleaseBuild != null) {
            if (extension.debug.enable.get()) {
                taskPreReleaseBuild.dependsOn('taskUnCommentDebug')
            } else {
                taskPreReleaseBuild.dependsOn('taskCommentDebug')
            }
        }
    }
    private static void tasksProjectRegisterInput(Project project, Extension extension) {
        if(!extension.configuration.library.get()){
            def classesToImport = []
            project.getParent().allprojects { lib ->
                Extension libExtension = lib.extensions.findByName(EXTENSION_NAME)
                if((libExtension != null)&&libExtension.configuration.library.get()){
                    TaskImportClassToAllDebug libTask = lib.tasks.findByName('taskImportClassToAllDebug')
                    if(libTask != null){
                        classesToImport.addAll(libTask.classesToImport)
                    }
                }
            }
            TaskImportClassToAllDebug projectTask = project.tasks.findByName('taskImportClassToAllDebug')
            if(projectTask.classesToImport != null){
                classesToImport.addAll(projectTask.classesToImport)
            }
            if(classesToImport.isEmpty()){
                projectTask.enabled = false
            }
            else{
                projectTask.classesToImport = classesToImport.toSet()
            }
        }
    }

    private static void configureAndroidApp(Project project, def android, Extension extension) {
        extension.initVersionName()
        android.defaultConfig {
            applicationId extension.configuration.applicationId.get()
            versionName extension.version.name.get()
            versionCode extension.version.value.get()
        }
        android.buildTypes {
            release {
                applicationIdSuffix BuildType.RELEASE.getExtension()
                buildConfigField "boolean", "DEBUG_ONLY", String.valueOf(extension.debug.enable.get())
            }
            debug {
                applicationIdSuffix BuildType.DEBUG.getExtension()
                buildConfigField "boolean", "DEBUG_ONLY", "true"
            }
        }
        android.lintOptions {
            checkReleaseBuilds extension.lint.checkReleaseBuilds.get()
            abortOnError extension.lint.abortOnError.get()
        }
    }
    private static Extension findRootProjectExtension(Project project){
        return project.getParent().allprojects.findResult {p ->
            Extension rootExtension = p.extensions.findByName(EXTENSION_NAME)
            if((rootExtension != null) && !rootExtension.configuration.library.get()){
                return rootExtension
            }
            else{
                return null
            }
        }
    }
    private static void configureAndroidLib(Project project, def android, Extension extension) {
        Extension rootExtension = findRootProjectExtension(project)
        if(rootExtension != null){
            extension.debug.enable.set(rootExtension.debug.enable.get())
        }
        android.defaultConfig {

        }
        android.buildTypes {
            release {
                minifyEnabled false
                buildConfigField "boolean", "DEBUG_ONLY", String.valueOf(extension.debug.enable.get())
            }
            debug {
                minifyEnabled false
                buildConfigField "boolean", "DEBUG_ONLY", "true"
            }
        }
    }
    private static void configureProguardApp(Project project, def android, Extension extension) {
        android.buildTypes.release {
            def proguardPath = project.rootDir.getParent() + normalizePath('/lib_java_android/lib/proguard/')
            proguardFiles proguardPath + 'proguard-android-optimize.txt'
            proguardFiles proguardPath + 'proguard-rules.pro'
            if (extension.debug.enable.get()) {
                project.logger.warn('************** DEBUG IS ACTIVE ON RELEASE **************')
                if(extension.debug.keepLog.get()) {
                    proguardFiles proguardPath + 'proguard-rules_release_debug_keep.pro'
                }
                else {
                    proguardFiles proguardPath + 'proguard-rules_release_debug_remove.pro'
                }
                if (extension.debug.repackage.get()) {
                    proguardFiles proguardPath + 'proguard-rules_release_repackage.pro'
                    if (extension.debug.keepSourceFile.get()) {
                        proguardFiles proguardPath + 'proguard-rules_release_keepSourceFile.pro'
                    }
                }
                if (!extension.debug.obfuscate.get()) {
                    proguardFiles proguardPath + 'proguard-rules_release_disallowObfusc.pro'
                }
            }
            else {
                proguardFiles proguardPath + 'proguard-rules_release_repackage.pro'
                proguardFiles proguardPath + 'proguard-rules_release_debug_remove.pro'
                if(!extension.release.obfuscate.get()){
                    project.logger.warn('************** RELEASE IS NOT OBFUSCATED **************')
                    proguardFiles proguardPath + 'proguard-rules_release_disallowObfusc.pro'
                }
            }
            if(extension.configuration.proguardPaths.isPresent()){
                extension.configuration.proguardPaths.get().each {
                    proguardFiles normalizePath(it)
                }
            }
        }
    }
    static String normalizePath(String path) {
        if (File.separator != "/") {
            path = path.replace("/", File.separator)
        }
        if (File.separator != "\\") {
            path = path.replace("\\", File.separator)
        }
        return path

    }
}
