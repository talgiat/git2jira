# git2jira

Retrieves information about git branches from jira as long a the branches naming adheres to the following convention: branch name is the jira story or bug key. For example for media express branch names are mex-1234, mex-2001 etc. 

## Usage

$ lein run --git-command git_branch_comment --dir git_project_dir --credentials jira_credentails --key jira_project_key

## Options

git_branch_comment - a git branch command to list all branches you want to lookup in jira
git_project_dir - a path to your git project that is in jira
jira_credentials - a jira user credentals that can use the jira API and read issues data. Format is username:password
jira_project_key - the jira project key prefix (e.g. mex,rcon)

## Examples
lein run --git-command "git branch -r" --dir /Users/talgiat/dev/mex --credentials mex_ro/mex_ro --key mex