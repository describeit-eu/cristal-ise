package eu.describeit.cristalise.kernel.module

StateMachine(name: 'Default', version: 'v0') {
  transition('Activate', [origin: 'Waiting', target: 'Active'])
  transition('Done', [origin: 'Active', target: 'Finished']) {
    schema(name: '${SchemaType}', version: '${SchemaVersion}')
    script(name: '${ScriptName}', version: '${ScriptVersion}')
    query(name: '${QueryName}', version: '${QueryVersion}')
  }
  transition('Lock', [origin: 'Active', target: 'Locked']) {
    property reservation: 'set'
  }
  transition('Complete', [origin: 'Locked', target: 'Finished']) {
    property(reservation: 'clear')
    schema(name: '${SchemaType}', version: '${SchemaVersion}')
    script(name: '${ScriptName}', version: '${ScriptVersion}')
    query(name: '${QueryName}', version: '${QueryVersion}')
  }
  transition('Suspend', [origin: 'Locked', target: 'Suspended']) {
    schema(name: 'Errors', version: '0')
  }
  transition('Resume', [origin: 'Suspended', target: 'Locked']) {
    property(reservation: 'preserve')
  }

  initialState('Waiting')
  finishingState('Finished')
}

StateMachine(name: 'Simple', version: 'v0') {
  transition('Activate', [origin: 'Waiting', target: 'Active'])
  transition('Done', [origin: 'Active', target: 'Finished']) {
    schema(name: '${SchemaType}', version: '${SchemaVersion}')
    script(name: '${ScriptName}', version: '${ScriptVersion}')
    query(name: '${QueryName}', version: '${QueryVersion}')
  }

  initialState('Waiting')
  finishingState('Finished')
}
