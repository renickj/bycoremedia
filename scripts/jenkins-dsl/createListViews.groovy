listView('DEV') {
  filterBuildQueue(true)
  filterExecutors(true)
  jobs {
    regex('.*DEV.*')
  }
  columns {
    status()
    weather()
    name()
    lastSuccess()
    lastFailure()
    lastDuration()
    buildButton()
  }
}

listView('TEST') {
  filterBuildQueue(true)
  filterExecutors(true)
  jobs {
    regex('.*TEST.*|.*DMDR.*')
  }
  columns {
    status()
    weather()
    name()
    lastSuccess()
    lastFailure()
    lastDuration()
    buildButton()
  }
}

listView('PROD') {
  filterBuildQueue(true)
  filterExecutors(true)
  jobs {
    regex('.*PROD.*')
  }
  columns {
    status()
    weather()
    name()
    lastSuccess()
    lastFailure()
    lastDuration()
    buildButton()
  }
}

listView('_GENERAL') {
  filterBuildQueue(true)
  filterExecutors(true)
  jobs {
    regex('.*upload-content-to-git.*|.*Build_LiveContext.*|')
  }
  columns {
    status()
    weather()
    name()
    lastSuccess()
    lastFailure()
    lastDuration()
    buildButton()
  }
}