// Script that is installed as a cronjob in scriptrunner


import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.bc.user.UserService
import com.atlassian.crowd.embedded.impl.ImmutableUser
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserManager
import com.atlassian.jira.security.login.LoginManager
import com.atlassian.jira.bc.security.login.LoginInfo
import com.atlassian.jira.user.ApplicationUsers
import com.atlassian.crowd.embedded.api.User


UserManager userManager = ComponentAccessor.getUserManager()
def loginManager = ComponentAccessor.getComponentOfType(LoginManager.class)
UserService userService = ComponentAccessor.getComponent(UserService)
ApplicationUser updateUser
UserService.UpdateUserValidationResult updateUserValidationResult
def users = userManager.getUsers().findAll{user -> (user.isActive())}

def whitelistedUsers = ["user@email.com"]

for (user in users)
{
    if (whitelistedUsers.contains(user.getEmailAddress()))
        continue
    def lastLoginMillis = loginManager.getLoginInfo(user.name).getLastLoginTime()
    def disable = false
    if(!lastLoginMillis)
    {
    //user has never logged in
    	disable = false
    }
    else
    {
        Date cutoff = new Date().minus(90)
        Date last = new Date(lastLoginMillis)
        if(last.before(cutoff))
        {
        	disable = true
        }
    }
    if(disable == true)
    {
        def userAsImmutable = new ImmutableUser(user.directoryId, user.name, user.displayName, user.emailAddress, false)
        updateUser = ApplicationUsers.from(userAsImmutable)
        updateUserValidationResult = userService.validateUpdateUser(updateUser)
        if (updateUserValidationResult.isValid()) 
        {
            userService.updateUser(updateUserValidationResult)
            log.info "Deactivated ${user.name}"
        }
        else
        {
        	log.error "Failed to deactivate ${user.name}"
        }
    }
}
