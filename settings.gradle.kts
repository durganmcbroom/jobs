
rootProject.name = "jobs"
include("progress")
include("logging")
include("progress:bars")
findProject(":progress:bars")?.name = "bars"
include("progress:simple")
findProject(":progress:simple")?.name = "simple"
