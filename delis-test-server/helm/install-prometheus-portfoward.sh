kubectl --namespace monitoring port-forward \
    $(kubectl get pod --namespace monitoring -l app=prometheus -o template --template "{{(index .items 0).metadata.name}}") \
    9090:9090