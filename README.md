# minimalist-fulcro-template-fly

This is a clone on [minimalist-fulcro-template](https://github.com/holyjak/minimalist-fulcro-template) extended with PostgreSQL and deploy to Fly.io.

Key differences:

1. You need to run `docker compose up` in this folder to start PostgreSQL
2. Evaluating `com.example.server.main` will not only start the server but
   also apply DB migrations from `resources/db-init.sql`
3. Pathom resolvers can use `(:conn env)` with `next.jdbc/execute!` to run queries
4. You must manually call `(start)` in the main ns to start the server

## Operation

### Local

#### Run via docker

```shell
docker compose up        # start Postgres
docker build -t ex-fly . # build the image
# Run the image, pointing to Postgres running on the host:
docker run --rm -it -p 9999:8008 \
  -e DATABASE_URL="jdbc:postgresql://postgres:FulcroRulez@host.docker.internal:5432/postgres"  \
  ex-fly:latest
```

### Fly

#### Fly setup

1. `fly launch --name mft-fly --no-deploy` - here we choose the app name _mft-fly_
2. `fly postgres  create` and choose app name, e.g. _mft-fly-db_
   This will print a connect string such as `postgres://postgres:sxRhUb2HlJUjSuc@mft-fly-db.internal:5432`
3. Set the DB url env var (see the `config` ns) for the app using the value: `fly secrets set -a mft-fly DATABASE_URL="postgres://postgres:sxRhUb2HlJUjSuc@mft-fly-db.internal:5432"`
4. Deploy: `fly deploy`
5. Access your deployed app under its name, here: https://mft-fly.fly.dev/
6. For Calva, the server is started automatically during jack-in
7. Factor out `resolvers` ns & make it so that you only need to load `pathom` ns to get resolver changes in

# Original minimalist-fulcro-template instructions

A template for starting a new, minimalistic, full-stack Fulcro application. Intended for playing with and learning Fulcro, not for production apps, and therefore simpler than the official [fulcro-template](https://github.com/fulcrologic/fulcro-template). It is a good starting point for your learning projects that is hopefully simple enough for you to understand.

TIP: For an even simpler template with an in-browser backend, see [minimalist-fulcro-template-backendless](https://github.com/holyjak/minimalist-fulcro-template-backendless).

## Creating a new application from the template

[Download](https://github.com/holyjak/minimalist-fulcro-template/archive/refs/heads/main.zip) or clone this repository to your computer and start hacking away.

## Explanation

You will run shadow-cljs, which will watch, compile, and update the sources and separately you will start a HTTP server from the REPL to serve the application and process Pathom requests.

## Usage

Prerequisites: [same as shadow-cljs'](https://github.com/thheller/shadow-cljs#requirements).

First, install frontend dependencies via npm, yarn, or similar:

    npm install # or yarn install # reportedly yarn < v3

then start the application either via

    npx shadow-cljs watch main

or, if you have [Babashka](https://babashka.org/) installed, via

    bb run

NOTE: For Calva, it is best to start a client build and REPL from the editor - [run Jack-in](https://calva.io/connect/#jack-in-let-calva-start-the-repl-for-you), selecting _shadow-cljs_ then the `:main` build. 

Now **start the server**: load `com.example.server.main` into the Shadow REPL - this will also evaluate the `(defonce stop-fn (atom start))` line, starting the server.

NOTE: Now if you ever change Pathom resolvers or something, run the restart code in the `(comment ...)` in the main ns.

Finally, navigate to http://localhost:8008. Note: You can switch to the browser REPL by evaluating `(shadow/repl :main)` in the REPL. (Calva does the latter for you).

### Create a standalone build

You can also compile the sources into a directory via

    npx shadow-cljs release main
    # or: bb build

## Why is this not suitable for production?

No thought was given to security, performance, monitoring, error tracking and other important production concerns. It also bakes in fulcro-troubleshooting, which you do not want unnecessarily increasing your bundle size in production settings. So if you want to use the template as a starting point for a production application, you will need to add those yourself.

## License

Copyleft © 2022 Jakub Holý

Distributed under the [Unlicense](https://unlicense.org/).
