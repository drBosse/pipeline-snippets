# pipeline-snippets
A small collection of groovy snippets to use w Jenkins Pipeline Plugin

In most cases the intended use is via the dsl files in the job directory

## Usage

Set a jenkins job for generating dsl jobs using this repository. Include all dsl files in the job directory (job/*.dsl). The preferred way is to set up this job to trigger on changes in the repository thus regenerate the jobs on commits.
