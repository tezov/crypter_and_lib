/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

import groovy.io.FileType
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.impldep.com.fasterxml.jackson.core.format.InputAccessor

import javax.inject.Inject

abstract class TaskImportClassToAllDebug extends DefaultTask {
    @InputDirectory
    File source = null
    @Input
    Collection<String> classesToImport = null
    @TaskAction
    void action() throws IOException {
        if((source != null) && (classesToImport != null)) {
            importToJavaFiles()
//            importToKotlinFiles()
        }
    }
    private importToJavaFiles(){
        def anyChar = /.*?/
        def anySpace = /(?:\t| )*/
        def startLine = /^${anySpace}/
        def endLine = /\R/

        def findImportedClassTemplate = /^${anySpace}import${anySpace}#;${anySpace}$/
        def importClassTemplate = "\nimport #;"
        def findPackageStart = /${startLine}(package ${anySpace}${anyChar};${endLine})/

        classesToImport = classesToImport.collect { classToImport -> classToImport.replace('.', '\\.') }
        def files = []
        source.eachFileRecurse (FileType.FILES) { file ->
            if(file.name.endsWith('.java')) {
                files.add(file)
            }
        }
        files.each { file ->
            def classesNotFound = classesToImport.collect()
            file.readLines().each { line ->
                classesNotFound.find { classToImport ->
                    def findImportedClass = findImportedClassTemplate.replace('#', classToImport)
                    if (line.matches(findImportedClass)) {
                        classesNotFound.remove(classToImport)
                        return true
                    } else return false
                }
            }
            if (!classesNotFound.isEmpty()) {
                def insertClassesToImport = /\1/ + classesNotFound.collect {
                    classToImport -> importClassTemplate.replace('#', classToImport)
                }.join()
                try {
                    ant.replaceregexp(
                            file: file,
                            match: findPackageStart,
                            replace: insertClassesToImport,
                            flags: 'gm',
                            byLine: false)
                }
                catch (Exception e) {
                    println "!!! addImportClassInAllClass Failed " + e.getClass().getSimpleName() + " for:" + file.path
                }
            }
        }

    }
    private importToKotlinFiles(){
        def anyChar = /.*?/
        def anySpace = /(?:\t| )*/
        def startLine = /^${anySpace}/
        def endLine = /\R/

        def findImportedClassTemplate = /^${anySpace}import${anySpace}#${anySpace}$/
        def importClassTemplate = "\nimport #"
        def findPackageStart = /${startLine}(package ${anySpace}${anyChar}${endLine})/

        classesToImport = classesToImport.collect { classToImport -> classToImport.replace('.', '\\.') }
        def files = []
        source.eachFileRecurse (FileType.FILES) { file ->
            if(file.name.endsWith('.kt')) {
                files.add(file)
            }
        }
        files.each { file ->
            def classesNotFound = classesToImport.collect()
            file.readLines().each { line ->
                classesNotFound.find { classToImport ->
                    def findImportedClass = findImportedClassTemplate.replace('#', classToImport)
                    if (line.matches(findImportedClass)) {
                        classesNotFound.remove(classToImport)
                        return true
                    } else return false
                }
            }
            if (!classesNotFound.isEmpty()) {
                def insertClassesToImport = /\1/ + classesNotFound.collect {
                    classToImport -> importClassTemplate.replace('#', classToImport)
                }.join()
                try {
                    ant.replaceregexp(
                            file: file,
                            match: findPackageStart,
                            replace: insertClassesToImport,
                            flags: 'gm',
                            byLine: false)
                }
                catch (Exception e) {
                    println "!!! addImportClassInAllClass Failed " + e.getClass().getSimpleName() + " for:" + file.path
                }
            }
        }

    }
}