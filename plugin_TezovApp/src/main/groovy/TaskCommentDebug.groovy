
/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileTree
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import groovy.io.FileType

import javax.inject.Inject

abstract class TaskCommentDebug extends DefaultTask {
    @InputDirectory
    File source = null
    @TaskAction
    void action() throws IOException {
        if(source != null) {
            commentJavaFiles()
            commentKotlinFiles()
        }
    }
    private commentJavaFiles(){
        def anySpace = /(?:\t| )*/
        def anyCharMultiLine = /(?:.|\R)*?/
        def startLine = /^${anySpace}/
        def endLine = /${anySpace}\R/

        def tag = "debug"
        def commentOpen = /\/\*#-${tag}-> /
        def commentClose = / <-${tag}-#\*\//

        def tagOpen = /(?:(?:DebugException)|(?:DebugLog)|(?:DebugTrack))\.start\(\)/
        def tagClose = /\.end\(\);/

        def find = /${startLine}(${tagOpen}${anyCharMultiLine}${tagClose})${endLine}/
        def replace = /${commentOpen}\1${commentClose}/ + "\n"

        def files = []
        source.eachFileRecurse (FileType.FILES) { file ->
            if(file.name.endsWith('.java')) {
                files.add(file)
            }
        }
        files.each { file ->
            try {
                ant.replaceregexp(
                        file: file,
                        match: find,
                        replace: replace,
                        flags: 'gm',
                        byLine: false)
            }
            catch (Exception e) {
                println "!!! commentDebug Failed " + e.getClass().getSimpleName() + " for:" + file.path
            }
        }
    }
    private commentKotlinFiles(){
        def anySpace = /(?:\t| )*/
        def anyCharMultiLine = /(?:.|\R)*?/
        def startLine = /^${anySpace}/
        def endLine = /${anySpace}\R/

        def tag = "debug"
        def commentOpen = /\/\*#-${tag}-> /
        def commentClose = / <-${tag}-#\*\//

        def tagOpen = /(?:(?:DebugException)|(?:DebugLog)|(?:DebugTrack))\.start\(\)/
        def tagClose = /\.end\(\);?/

        def find = /${startLine}(${tagOpen}${anyCharMultiLine}${tagClose})${endLine}/
        def replace = /${commentOpen}\1${commentClose}/ + "\n"

        def files = []
        source.eachFileRecurse (FileType.FILES) { file ->
            if(file.name.endsWith('.kt')) {
                files.add(file)
            }
        }
        files.each { file ->
            try {
                ant.replaceregexp(
                        file: file,
                        match: find,
                        replace: replace,
                        flags: 'gm',
                        byLine: false)
            }
            catch (Exception e) {
                println "!!! commentDebug Failed " + e.getClass().getSimpleName() + " for:" + file.path
            }
        }
    }

}