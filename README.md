# Atlassian Jira Software Data Center

[Jira](https://www.atlassian.com/software/jira) is a tool used for bug tracking, issue tracking, and project management. It is developed and published by the australian software company **Atlassian**.

## Introduction

This chart bootstraps a [Jira Software](https://hub.docker.com/r/atlassian/jira-software/) deployment on a [Kubernetes](http://kubernetes.io) cluster using the [Helm](https://helm.sh) package manager. We have also added a backup container that does a backup of all the data on disk and in the postgres db. The image for that container needs to be pushed manually to harbor.

## Chart deployment

### Production
The cluster is deployed in region us-central-1. The cluster is managed by [Gardener](https://dashboard.garden.canary.k8s.ondemand.com/namespace/garden-eureka/shoots/jira/).
- Install the helm chart
   ```console
   helm install --name=jira-software charts/ -f charts/values.test.yaml
   ```
  - Login, setup and reindex Jira.
  
- Upgrade the helm chart 
  ```console
  helm upgrade jira-software charts/ -f charts/values.test.yaml
  ```

### Test/Development
1. Remove `/charts` from [.helmignore](charts/.helmignore). This will include a cluster PG instance.
2. Run
   ```console
   helm install --name=jira-test1 charts/ -f charts/values.test.yaml
   ```
