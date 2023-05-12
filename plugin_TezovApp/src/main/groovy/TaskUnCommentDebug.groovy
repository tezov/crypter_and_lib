/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

import groovy.io.FileType
import org.gradle.api.DefaultTask
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

abstract class TaskUnCommentDebug extends DefaultTask {
    @InputDirectory
    File source = null
    @TaskAction
    void action() throws IOException {
        if(source != null) {
            unCommentJavaFiles()
            unCommentKotlinFiles()
        }
    }
    private unCommentJavaFiles(){
        def anySpace = /(?:\t| )*/
        def anyCharMultiLine = /(?:.|\R)*?/
        def startLine = /^${anySpace}/

        def tag = "debug"
        def commentOpen = /\/\*#-${tag}-> /
        def commentClose = / <-${tag}-#\*\//

        def find = /${startLine}${commentOpen}(${anyCharMultiLine})${commentClose}/
        def replace = /\1/
        def files = []
        source.eachFileRecurse(FileType.FILES) { file ->
            if (file.name.endsWith('.java')) {
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
                println "!!! unCommentDebug Failed for:" + file.path + ' error:' + e.getClass().simpleName
            }
        }
    }
    private unCommentKotlinFiles(){
        def anySpace = /(?:\t| )*/
        def anyCharMultiLine = /(?:.|\R)*?/
        def startLine = /^${anySpace}/

        def tag = "debug"
        def commentOpen = /\/\*#-${tag}-> /
        def commentClose = / <-${tag}-#\*\//

        def find = /${startLine}${commentOpen}(${anyCharMultiLine})${commentClose}/
        def replace = /\1/
        def files = []
        source.eachFileRecurse(FileType.FILES) { file ->
            if (file.name.endsWith('.kt')) {
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
                println "!!! unCommentDebug Failed for:" + file.path + ' error:' + e.getClass().simpleName
            }
        }
    }

}