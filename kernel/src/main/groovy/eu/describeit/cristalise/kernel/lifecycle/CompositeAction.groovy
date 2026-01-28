package eu.describeit.cristalise.kernel.lifecycle

import eu.describeit.cristalise.kernel.persistency.RepositoryWrapper
import eu.describeit.cristalise.kernel.persistency.domain.JobDO
import groovy.transform.CompileStatic
import io.vertx.core.Future

@CompileStatic
interface CompositeAction extends Action {
  Future<Void> initialise(RepositoryWrapper storage)

  List<Action> getActions()
  Future<Action> findAction(String actionPath)
  Future<JobDO> calculateNextJobs()
}
