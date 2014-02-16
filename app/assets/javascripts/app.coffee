# -----------------------------------------------
# MAIN
# -----------------------------------------------
# DISCLAMER :
# If you're used to Backbone.js, you may be
# confused by the absence of models, but the goal
# of this sample is to demonstrate some features
# of Play including the template engine.
# I'm not using client-side templating nor models
# for this purpose, and I do not recommend this
# behavior for real life projects.
# -----------------------------------------------

# Just a log helper
log = (args...) ->
  console.log.apply console, args if console.log?

