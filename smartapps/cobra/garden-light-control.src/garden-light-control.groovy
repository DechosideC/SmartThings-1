/**
 *  Garden Light Control
 *
 *  Copyright 2017 Andrew Parker
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
 
 
definition(
    name: "Garden Light Control",
    namespace: "Cobra",
    author: "Andrew Parker",
    description: "Controls a set of sockets for garden lights turn on at sunset and off at a certain time",
    category: "",
    iconUrl: "http://54.246.165.27/img/icons/gardenlights.png",
    iconX2Url: "http://54.246.165.27/img/icons/gardenlights.png",
    iconX3Url: "http://54.246.165.27/img/icons/gardenlights.png")
 

preferences {

section() {
   
        paragraph image: "http://54.246.165.27/img/icons/cobra3.png",
     
                  "Version: 1.0.0 - Brought to you by Cobra"
    }
section() {
    
        paragraph image: "http://54.246.165.27/img/icons/gardenlights.png",
                  title: "Garden Light Control",
                  required: false,
                  "Controls a set of sockets for garden lights turn on at sunset and off at a certain time"
    }


	section(""){
            input "enableApp", "bool", title: "Enable App", required: true, defaultValue: true
        }
     	section(""){     
        input (name: "switch1", type: "capability.switch", title: "Control these switches", multiple: true, required: false)   
        input "offset", "number", title: "Turn on this many minutes before sunset"
        input (name: "offTime", title: "Turn Off - At what time?", type: "time",  required: true)
        }
}

def installed() {
	initialize()
}

def updated() {
	unsubscribe()
	initialize()
}

def initialize() {
		log.debug "Initialised with settings: ${settings}"

// Check if app is enabled
	appEnable()
    
// Subscriptions
	subscribe(location, "sunsetTime", sunsetTimeHandler)
	schedule(offTime, offNow)
 }
 
 
 
def sunsetTimeHandler(evt) {
if(state.appGo == true){
    scheduleTurnOn(evt.value)
	}
    else if(state.appGo == false){
    log.info "App is diaabled so doing nothing"
}
}
def scheduleTurnOn(sunsetString) {
    def sunsetTime = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", sunsetString)
    def timeBeforeSunset = new Date(sunsetTime.time - (offset * 60 * 1000))

    log.debug "Scheduling for: $timeBeforeSunset (sunset is $sunsetTime)"
    runOnce(timeBeforeSunset, turnOn)
}


def turnOn() {
    log.debug "Turning on lights"
    switch1.on()
	}


def offNow(evt){
 log.debug "Turning off lights"
    switch1.off()
}


// Enable/Disable App
def appEnable(){
	if (enableApp == true){ 
    state.appGo = true
    log.debug "App is Enabled" }
    else if (enableApp == false){ 
    state.appGo = false
    log.debug "App is Disabled" }
    
 }