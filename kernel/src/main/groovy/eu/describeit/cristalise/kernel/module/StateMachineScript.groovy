package eu.describeit.cristalise.kernel.module

StateMachine(name: 'Default', version: 'v0') {
  transition('Done', [origin: 'Waiting', target: 'Finished']) {
    outcome(name: '\${SchemaType}', version: '\${SchemaVersion}')
    script(name: '\${ScriptName}', version: '\${ScriptVersion}')
    query(name: '\${QueryName}', version: '\${QueryVersion}')
  }
  transition('Start', [origin: 'Waiting', target: 'Started']) {
    property reservation: 'set'
  }
  transition('Complete', [origin: 'Started', target: 'Finished']) {
    property(reservation: 'clear')
    outcome(name: '\${SchemaType}', version: '\${SchemaVersion}')
    script(name: '\${ScriptName}', version: '\${ScriptVersion}')
    query(name: '\${QueryName}', version: '\${QueryVersion}')
  }
  transition('Suspend', [origin: 'Started', target: 'Suspended']) {
    outcome(name: 'Errors', version: '0')
  }
  transition('Resume', [origin: 'Suspended', target: 'Started']) {
    property(reservation: 'preserve')
  }

  initialState('Waiting')
  finishingState('Finished')
}

StateMachine(name: 'Simple', version: 'v0') {
  transition('Done', [origin: 'Waiting', target: 'Finished']) {
    outcome(name: '${SchemaType}', version: '${SchemaVersion}')
    script(name: '${ScriptName}', version: '${ScriptVersion}')
    query(name: '${QueryName}', version: '${QueryVersion}')
  }

  initialState('Waiting')
  finishingState('Finished')
}
