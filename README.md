![OSS Review Toolkit Logo](./logos/ort.png)

&nbsp;

| Linux (OpenJDK 11)             | Windows (Oracle JDK 11)         | JitPack (OpenJDK 8)             |
| :----------------------------- | :------------------------------ | :------------------------------ |
| [![Linux build status][1]][2]  | [![Windows build status][3]][4] | [![JitPack build status][5]][6] |
| [![Linux code coverage][7]][8] |                                 |                                 |

| License status           | Code quality      | TODOs              | Interact with us!              |
| :----------------------- | :---------------- | :----------------- | :----------------------------- |
| [![REUSE status][9]][10] | [![LGTM][11]][12] | [![TODOs][13]][14] | [![ort-talk][15]][16]          |

[1]: https://travis-ci.com/oss-review-toolkit/ort.svg?branch=master
[2]: https://travis-ci.com/oss-review-toolkit/ort
[3]: https://ci.appveyor.com/api/projects/status/8oh5ld40c8h19jr5/branch/master?svg=true
[4]: https://ci.appveyor.com/project/oss-review-toolkit/ort/branch/master
[5]: https://jitpack.io/v/oss-review-toolkit/ort.svg
[6]: https://jitpack.io/#oss-review-toolkit/ort
[7]: https://codecov.io/gh/oss-review-toolkit/ort/branch/master/graph/badge.svg
[8]: https://codecov.io/gh/oss-review-toolkit/ort/
[9]: https://api.reuse.software/badge/github.com/oss-review-toolkit/ort
[10]: https://api.reuse.software/info/github.com/oss-review-toolkit/ort
[11]: https://img.shields.io/lgtm/alerts/g/oss-review-toolkit/ort.svg?logo=lgtm&logoWidth=18
[12]: https://lgtm.com/projects/g/oss-review-toolkit/ort/alerts/
[13]: https://badgen.net/https/api.tickgit.com/badgen/github.com/oss-review-toolkit/ort
[14]: https://www.tickgit.com/browse?repo=github.com/oss-review-toolkit/ort
[15]: https://img.shields.io/badge/slack-ort--talk-blue.svg?longCache=true&logo=slack
[16]: https://join.slack.com/t/ort-talk/shared_invite/enQtMzk3MDU5Njk0Njc1LThiNmJmMjc5YWUxZTU4OGI5NmY3YTFlZWM5YTliZmY5ODc0MGMyOWIwYmRiZWFmNGMzOWY2NzVhYTI0NTJkNmY

# Introduction

The OSS Review Toolkit (ORT) aims to assist with the tasks that commonly need to be performed in the context of license
compliance checks, especially for (but not limited to) Free and Open Source Software dependencies.

It does so by orchestrating a _highly customizable_ pipeline of tools that _abstract away_ the underlying services.
These tools are implemented as libraries (for programmatic use) and exposed via a command line interface (for scripted
use):

* [_Analyzer_](#analyzer) - determines the dependencies of projects and their meta-data, abstracting which package
  managers or build systems are actually being used.
* [_Downloader_](#downloader) - fetches all source code of the projects and their dependencies, abstracting which
  Version Control System (VCS) or other means are used to retrieve the source code.
* [_Scanner_](#scanner) - uses configured source code scanners to detect license / copyright findings, abstracting
  the type of scanner.
* [_Evaluator_](#evaluator) - evaluates license / copyright findings against customizable policy rules and license
  classifications.
* [_Reporter_](#reporter) - presents results in various formats such as visual reports, Open Source notices or
  Bill-Of-Materials (BOMs) to easily identify dependencies, licenses, copyrights or policy rule violations.

The following tools are [planned](https://github.com/oss-review-toolkit/ort/projects/1) but not yet available:

* _Advisor_ - retrieves security advisories based on the Analyzer result.
* _Documenter_ - generates the final outcome of the review process incl. legal conclusions, e.g. annotated
  [SPDX](https://spdx.dev/) files that can be included into the distribution.

# Installation

## From binaries

Preliminary binary artifacts for ORT are currently available via
[JitPack](https://jitpack.io/#oss-review-toolkit/ort). Please note that due to limitations with the JitPack build
environment, the reporter is not able to create the Web App report.

## From sources

Install the following basic prerequisites:

* Git (any recent version will do).

Then clone this repository. If you intend to run tests, you need to clone with submodules by running
`git clone --recurse-submodules`. If you have already cloned non-recursively, you can initialize submodules afterwards
by running `git submodule update --init --recursive`.

### Build using Docker

Install the following basic prerequisites:

* Docker (and ensure its daemon is running).

Change into the directory with ORT's source code and run `docker build -t ort .`.

### Build natively

Install these additional prerequisites:

* OpenJDK 8 or Oracle JDK 8u161 or later (not the JRE as you need the `javac` compiler); also remember to set the
  `JAVA_HOME` environment variable accordingly.

Change into the directory with ORT's source code and run `./gradlew installDist` (on the first run this will bootstrap
Gradle and download all required dependencies).

## Basic usage

ORT can now be run using

    ./cli/build/install/ort/bin/ort --help

Note that if you make any changes to ORT's source code, you would have to regenerate the distribution using the steps
above.

To avoid that, you can also build and run ORT in one go (if you have the prerequisites from the
[Build natively](#build-natively) section installed):

    ./gradlew cli:run --args="--help"

Note that in this case the working directory used by ORT is that of the `cli` project, not the directory `gradlew` is
located in (see https://github.com/gradle/gradle/issues/6074).

# Running the tools

Like for building ORT from sources you have the option to run ORT from a Docker image (which comes with all runtime
dependencies) or to run ORT natively (in which case some additional requirements need to be fulfilled).

## Run using Docker

After you have built the image as [described above](#build-using-docker), simply run
`docker run <DOCKER_ARGS> ort <ORT_ARGS>`. You typically use `<DOCKER_ARGS>` to mount the project directory to analyze
into the container for ORT to access it, like:

    docker run -v /workspace:/project ort --info analyze -f JSON -i /project -o /project/ort/analyzer

You can find further hints for using ORT with Docker in the [documentation](./docs/hints-for-use-with-docker.md).

## Run natively

First of all, make sure that the locale of your system is set to `en_US.UTF-8` as using other locales might lead to
issues with parsing the output of some external tools.

Then install any missing external command line tools as listed by

    ./cli/build/install/ort/bin/ort requirements

or

    ./gradlew cli:run --args="requirements"

Then run ORT like

    ./cli/build/install/ort/bin/ort --info analyze -f JSON -i /project -o /project/ort/analyzer

or

    ./gradlew cli:run --args="--info analyze -f JSON -i /project -o /project/ort/analyzer"

## Running on CI

A basic ORT pipeline (using the _analyzer_, _scanner_ and _reporter_) can easily be run on
[Jenkins CI](https://jenkins.io/) by using the [Jenkinsfile](./Jenkinsfile) in a (declarative)
[pipeline](https://jenkins.io/doc/book/pipeline/) job. Please see the [Jenkinsfile](./Jenkinsfile) itself for
documentation of the required Jenkins plugins. The job accepts various parameters that are translated to ORT command
line arguments. Additionally, one can trigger a downstream job which e.g. further processes scan results. Note that it
is the downstream job's responsibility to copy any artifacts it needs from the upstream job.

## Getting started

Please see [Getting Started](./docs/getting-started.md) for an introduction to the individual tools.

## Configuration

ORT looks for its configuration files in the directory pointed to by the `ORT_CONFIG_DIR` environment variable. If this
variable is not set, it defaults to the `config` directory below the directory pointed to by the `ORT_DATA_DIR`
environment variable, which in turn defaults to the `.ort` directory below the current user's home directory. See the
table below for the supported environment variables and their defaults:

| Name | Default value | Purpose |
| ---- | ------------- | ------- |
| ORT_DATA_DIR | `~/.ort` | All data, like caches, archives, storages (read & write) |
| ORT_CONFIG_DIR | `$ORT_DATA_DIR/config` | Configuration files, see below (read only) |

The following provides an overview of the various configuration files that can be used to customize ORT behavior:

[ORT configuration file](./model/src/main/resources/reference.conf)

The main configuration file for the operation of ORT. This configuration is maintained by an administrator who manages
the ORT instance. In contrast to the configuration files in the following, this file rarely changes once ORT is
operational.

| Format | Scope | Default location | Default value |
| ------ | ----- | ---------------- | ------------- |
| HOCON | Global | `$ORT_CONFIG_DIR/ort.conf` | Empty ([built-in](./model/src/main/resources/default.conf)) |

[Copyright garbage file](./docs/config-file-copyright-garbage-yml.md)

A list of copyright statements that are considered garbage, for example statements that were incorrectly classified as
copyrights by the scanner.

| Format | Scope | Default location | Default value |
| ------ | ----- | ---------------- | ------------- |
| YAML / JSON | Global | `$ORT_CONFIG_DIR/copyright-garbage.yml` | Empty (n/a) |

[Curations file](./docs/config-file-curations-yml.md)

A file to correct invalid or missing package metadata, and to set the concluded license for packages.

| Format | Scope | Default location | Default value |
| ------ | ----- | ---------------- | ------------- |
| YAML / JSON | Global | `$ORT_CONFIG_DIR/curations.yml` | Empty (n/a) |

[Custom license texts dir](./docs/dir-custom-license-texts.md)

A directory that contains license texts which are not provided by ORT.

| Format | Scope | Default location | Default value |
| ------ | ----- | ---------------- | ------------- |
| Text | Global | `$ORT_CONFIG_DIR/custom-license-texts/` | Empty (n/a) |

[How to fix text provider script](./docs/how-to-fix-text-provider-kts.md)

A Kotlin script that enables the injection of how-to-fix texts in markdown format for ORT issues into the reports.

| Format | Scope | Default location | Default value |
| ------ | ----- | ---------------- | ------------- |
| Kotlin script | Global | `$ORT_CONFIG_DIR/how-to-fix-text-provider.kts` | Empty (n/a) |

[License configuration file](./docs/config-file-licenses-yml.md)

A file that contains user-defined categorization of licenses.

| Format | Scope | Default location | Default value |
| ------ | ----- | ---------------- | ------------- |
| YAML / JSON | Global | `$ORT_CONFIG_DIR/licenses.yml` | Empty (n/a) |

[Resolution file](./docs/config-file-resolutions-yml.md)

Configurations to resolve any issues or rule violations by providing a mandatory reason, and an optional comment to
justify the resolution on a global scale.

| Format | Scope | Default location | Default value |
| ------ | ----- | ---------------- | ------------- |
| YAML / JSON | Global | `$ORT_CONFIG_DIR/resolutions.yml` | Empty (n/a) |

[Repository configuration file](./docs/config-file-ort-yml.md)

A configuration file, usually stored in the project's repository, for license finding curations, exclusions, and issues
or rule violations resolutions in the context of the repository.

| Format | Scope | Default location | Default value |
| ------ | ----- | ---------------- | ------------- |
| YAML / JSON | Repository (project) | `[analyzer-input-dir]/.ort.yml` | Empty (n/a) |

[Package configuration file / directory](./docs/config-file-package-configuration-yml.md)

A single file or a directory with multiple files containing configurations to set provenance-specific path excludes and
license finding curations for dependency packages to address issues found within a scan result. The `helper-cli`'s
[GeneratePackageConfigurationsCommand](./helper-cli/src/main/kotlin/commands/GeneratePackageConfigurationsCommand.kt)
can be used to populate a directory with template package configuration files.

| Format | Scope | Default location | Default value |
| ------ | ----- | ---------------- | ------------- |
| YAML / JSON | Package (dependency) | `$ORT_CONFIG_DIR/package-configurations/` | Empty (n/a) |

[Policy rules file](./docs/file-rules-kts.md)

The file containing any policy rule implementations to be used with the _evaluator_.

| Format | Scope | Default location | Default value |
| ------ | ----- | ---------------- | ------------- |
| Kotlin script (DSL) | Evaluator | `$ORT_CONFIG_DIR/rules.kts` | Empty (n/a) |

# Details on the tools

<a name="analyzer"></a>

[![Analyzer](./logos/analyzer.png)](./analyzer/src/main/kotlin)

The _analyzer_ is a Software Composition Analysis (SCA) tool that determines the dependencies of software projects
inside the specified input directory (`-i`). It does so by querying the detected package managers; **no modifications**
to your existing project source code, like applying build system plugins, are necessary for that to work. The tree of
transitive dependencies per project is written out as part of an
[OrtResult](https://github.com/oss-review-toolkit/ort/blob/master/model/src/main/kotlin/OrtResult.kt) in YAML (or
JSON, see `-f`) format to a file named `analyzer-result.yml` in the specified output directory (`-o`). The output file
exactly documents the status quo of all package-related meta-data. It can be further processed or manually edited before
passing it to one of the other tools.

Currently, the following package managers are supported:

* [Bower](http://bower.io/) (JavaScript)
* [Bundler](http://bundler.io/) (Ruby)
* [Cargo](https://doc.rust-lang.org/cargo/) (Rust)
* [Carthage](https://github.com/Carthage/Carthage) (iOS / Cocoa)
* [Conan](https://conan.io/) (C / C++, *experimental* as the VCS locations often times do not contain the actual source
  code, see [issue #2037](https://github.com/oss-review-toolkit/ort/issues/2037))
* [dep](https://golang.github.io/dep/) (Go)
* [DotNet](https://docs.microsoft.com/en-us/dotnet/core/tools/) (.NET, with currently some
  [limitations](https://github.com/oss-review-toolkit/ort/pull/1303#issue-253860146))
* [Glide](https://glide.sh/) (Go)
* [Godep](https://github.com/tools/godep) (Go)
* [GoMod](https://github.com/golang/go/wiki/Modules) (Go, *experimental* as only proxy-based source artifacts but no VCS
  locations are supported)
* [Gradle](https://gradle.org/) (Java)
* [Maven](http://maven.apache.org/) (Java)
* [NPM](https://www.npmjs.com/) (Node.js)
* [NuGet](https://www.nuget.org/) (.NET, with currently some
  [limitations](https://github.com/oss-review-toolkit/ort/pull/1303#issue-253860146))
* [Composer](https://getcomposer.org/) (PHP)
* [PIP](https://pip.pypa.io/) (Python)
* [Pipenv](https://pipenv.readthedocs.io/) (Python)
* [Pub](https://pub.dev/) (Dart / Flutter)
* [SBT](http://www.scala-sbt.org/) (Scala)
* [SPDX](https://spdx.dev/specifications/) (SPDX documents used to describe
  [projects](./analyzer/src/funTest/assets/projects/synthetic/spdx/project/project.spdx.yml) or
  [packages](./analyzer/src/funTest/assets/projects/synthetic/spdx/package/libs/curl/package.spdx.yml))
* [Stack](http://haskellstack.org/) (Haskell)
* [Yarn](https://yarnpkg.com/) (Node.js)

<a name="downloader">&nbsp;</a>

[![Downloader](./logos/downloader.png)](./downloader/src/main/kotlin)

Taking an ORT result file with an _analyzer_ result as the input (`-i`), the _downloader_ retrieves the source code of
all contained packages to the specified output directory (`-o`). The _downloader_ takes care of things like normalizing
URLs and using the [appropriate VCS tool](./downloader/src/main/kotlin/vcs) to checkout source code from version
control.

Currently, the following Version Control Systems (VCS) are supported:

* [CVS](https://en.wikipedia.org/wiki/Concurrent_Versions_System)
* [Git](https://git-scm.com/)
* [Git-Repo](https://source.android.com/setup/develop/repo)
* [Mercurial](https://www.mercurial-scm.org/)
* [Subversion](https://subversion.apache.org/)

<a name="scanner">&nbsp;</a>

[![Scanner](./logos/scanner.png)](./scanner/src/main/kotlin)

This tool wraps underlying license / copyright scanners with a common API so all supported scanners can be used in the
same way to easily run them and compare their results. If passed an ORT result file with an analyzer result (`-i`), the
_scanner_ will automatically download the sources of the dependencies via the _downloader_ and scan them afterwards.

We recommend to use ORT with one of the following scanners as their integration has been thoroughly tested:

* [ScanCode](https://github.com/nexB/scancode-toolkit)

Additionally, the following reference implementations exist:

* [Askalono](https://github.com/amzn/askalono)
* [lc](https://github.com/boyter/lc)
* [Licensee](https://github.com/benbalter/licensee)

For a comparison of some of these, see this
[Bachelor Thesis](https://osr.cs.fau.de/2019/08/07/final-thesis-a-comparison-study-of-open-source-license-crawler/).

## Storage Backends

In order to not download or scan any previously scanned sources again, the _scanner_ can use a storage backend to store
scan results for later reuse.

### Local File Storage

By default the _scanner_ stores scan results on the local file system in the current user's home directory (i.e.
`~/.ort/scanner/scan-results`) for later reuse. The storage directory can be customized by passing an ORT configuration
file (`-c`) that contains a respective local file storage configuration:

```hocon
ort {
  scanner {
    fileBasedStorage {
      backend {
        localFileStorage {
          directory = "/tmp/ort/scan-results"
          compression = false
        }
      }
    }
  }
}
```

### HTTP Storage

Any HTTP file server can be used to store scan results. Custom headers can be configured to provide authentication
credentials. For example, to use Artifactory to store scan results, use the following configuration:

```hocon
ort {
  scanner {
    fileBasedStorage {
      backend {
        httpFileStorage {
          url = "https://artifactory.domain.com/artifactory/repository/scan-results"
          headers {
            X-JFrog-Art-Api = "api-token"
          }
        }
      }
    }
  }
}
```

### PostgreSQL Storage

To use PostgreSQL for storing scan results you need at least version 9.4, create a database with the `client_encoding`
set to `UTF8`, and a configuration like the following:

```hocon
ort {
  scanner {
    postgresStorage {
      url = "jdbc:postgresql://example.com:5444/database"
      schema = "schema"
      username = "username"
      password = "password"
      sslmode = "verify-full"
    }
  }
}
```

While the specified schema already needs to exist, the _scanner_ will itself create a table called `scan_results` and
store the data in a [jsonb](https://www.postgresql.org/docs/current/datatype-json.html) column.

If you do not want to use SSL set the `sslmode` to `disable`, other possible values are explained in the
[documentation](https://jdbc.postgresql.org/documentation/head/ssl-client.html). For other supported configuration
options see [PostgresStorageConfiguration.kt](./model/src/main/kotlin/config/PostgresStorageConfiguration.kt).

<a name="evaluator">&nbsp;</a>

[![Evaluator](./logos/evaluator.png)](./evaluator/src/main/kotlin)

The _evaluator_ is used to perform custom license policy checks on scan results. The rules to check against are
implemented as scripts (currently Kotlin scripts, with a dedicated DSL, but support for other scripting can be added as
well. See [rules.kts](./examples/rules.kts) for an example file.

<a name="reporter">&nbsp;</a>

[![Reporter](./logos/reporter.png)](./reporter/src/main/kotlin)

The _reporter_ generates human-readable reports from the scan result file generated by the _scanner_ (`-s`). It is
designed to support multiple output formats.

Currently, the following report formats are supported (reporter names are case-insensitive):

* [Amazon OSS Attribution Builder](https://github.com/amzn/oss-attribution-builder) document (*experimental*, `-f AmazonOssAttributionBuilder`)
* [Antenna Attribution Document (PDF)](./docs/reporters/AntennaAttributionDocumentReporter.md) (`-f AntennaAttributionDocument`)
* [CycloneDX](https://cyclonedx.org/) BOM (`-f CycloneDx`)
* [Excel](https://products.office.com/excel) sheet (`-f Excel`)
* [GitLabLicenseModel](https://docs.gitlab.com/ee/ci/pipelines/job_artifacts.html#artifactsreportslicense_scanning-ultimate) (`-f GitLabLicenseModel`)
  * A nice tutorial video has been [published](https://youtu.be/dNmH_kYJ34g) by GitLab engineer @mokhan.
* [NOTICE](http://www.apache.org/dev/licensing-howto.html) file in two variants
  * List license texts and copyrights by package (`-f NoticeTemplate`)
  * Summarize all license texts and copyrights (`-f NoticeTemplate -O NoticeTemplate=template.id=summary`)
  * Customizable with [Apache Freemarker](https://freemarker.apache.org/) templates
* [SPDX Document](https://spdx.dev/specifications/), version 2.2 (`-f SpdxDocument`)
* Static HTML (`-f StaticHtml`)
* Web App (`-f WebApp`)

# System requirements

ORT is being continuously used on Linux, Windows and macOS by the
[core development team](https://github.com/orgs/oss-review-toolkit/teams/core-devs), so these operating systems are
considered to be well supported.

To run the ORT binaries (also see [Installation from binaries](#from-binaries)) at least a Java Runtime Environment
(JRE) version 8 is required, but using version 11 is recommended. Memory and CPU requirements vary depending on the size
and type of project(s) to analyze / scan, but the general recommendation is to configure the JRE with 8 GiB of memory
(`-Xmx=8g`) and to use a CPU with at least 4 cores.

If ORT requires external tools in order to analyze a project, these tools are listed by the `ort requirements` command.
If a package manager is not list listed there, support for it is integrated directly into ORT and does not require any
external tools to be installed.

# Development

ORT is written in [Kotlin](https://kotlinlang.org/) and uses [Gradle](https://gradle.org/) as the build system, with
[Kotlin script](https://docs.gradle.org/current/userguide/kotlin_dsl.html) instead of Groovy as the DSL.

When developing on the command line, use the committed
[Gradle wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) to bootstrap Gradle in the configured
version and execute any given tasks. The most important tasks for this project are:

| Task        | Purpose                                                           |
| ----------- | ----------------------------------------------------------------- |
| assemble    | Build the JAR artifacts for all projects                          |
| detekt      | Run static code analysis on all projects                          |
| test        | Run unit tests for all projects                                   |
| funTest     | Run functional tests for all projects                             |
| installDist | Build all projects and install the start scripts for distribution |

All contributions need to pass the `detekt`, `test` and `funTest` checks before they can be merged.

For IDE development we recommend the [IntelliJ IDEA Community Edition](https://www.jetbrains.com/idea/download/) which
can directly import the Gradle build files. After cloning the project's source code recursively, simply run IDEA and use
the following steps to import the project.

1. From the wizard dialog: Select *Import Project*.

   From a running IDEA instance: Select *File* -> *New* -> *Project from Existing Sources...*

2. Browse to ORT's source code directory and select either the `build.gradle.kts` or the `settings.gradle.kts` file.

3. In the *Import Project from Gradle* dialog select *Use auto-import* and leave all other settings at their defaults.

## Debugging

To set up a basic run configuration for debugging, navigate to `Main.kt` in the `cli` module and look for the
`fun main(args: Array<String>)` function. In the gutter next to it, a green "Play" icon should be displayed. Click on it
and select `Run 'org.ossreviewtoolkit.Main'` to run the entry point, which implicitly creates a run configuration.
Double-check that running ORT without any arguments will simply show the command line help in IDEA's *Run* tool window.
Finally, edit the created run configuration to your needs, e.g. by adding an argument and options to run a specific ORT
sub-command.

## Testing

For running tests and individual test cases from the IDE, the [kotest plugin](https://plugins.jetbrains.com/plugin/14080-kotest)
needs to be installed. Afterwards tests can be run via the green "Play" icon from the gutter as described above.

# License

Copyright (C) 2017-2020 HERE Europe B.V.

See the [LICENSE](./LICENSE) file in the root of this project for license details.

OSS Review Toolkit (ORT) is a [Linux Foundation project](https://www.linuxfoundation.org) and part of [ACT](https://automatecompliance.org/).
