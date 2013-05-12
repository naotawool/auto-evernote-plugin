package com.evernote.jenkins.evernote.auto.EvernoteGlobalConfiguration

def f=namespace(lib.FormTagLib)

f.section(title:"Evernote") {
    f.entry(title:_("Developer Token"), field:"developerToken", description:_("Please enter Everntoe developer token")) {
        f.textbox(default: "token")
    }
    f.entry(title:_("Use production environment"), field:"production", description:_("If not checked, using sandbox environment.")) {
        f.checkbox()
    }
}
