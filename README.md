# git2jira

Retrieves information about git branches from jira as long a branches names are jira issue keys. For example names are foo-1234, bar-2001 etc. By default the follwoing fields are retrieved: key, summary, status and fixVersions. Key will always be fetches as the first column, but you can pass the other fields as an argument to git2jira/branches-info function. The fields argument should be a clojure map of the form:
```
{ :field_name_keyword fn[fields] (;logic of how to extract the field from fields object in api response) 
  :another_field_name_keyword fn[fields] (;logic of how to extract this field) }
```
Take a look at core.clj field-formatter for an real exmaple.

## Usage
```bash
$ lein run --git-command git_branch_comment --dir git_project_dir --credentials jira_credentails --key jira_project_key --api-url jira_api_url
```
## Options

* git_branch_comment - a git branch command to list all branches you want to lookup in jira. Default is `git branch -r`
* git_project_dir - a path to your git project that is in jira
* jira_credentials - a jira user credentals that can use the jira API and read issues data. Format is username:password
* jira_project_key - the jira project key prefix (e.g. foo, bar)
* jira_api_url - the full url (without parameters) to you jira API. **Note:** currently only the rest api version 2 is supported. 

## Examples
```bash
lein run --git-command "git branch -r" --dir /path/to/project/folder --credentials jira_user/jira_password --key bar --api_url http://your.jira.domain/rest/api/2/search
```
