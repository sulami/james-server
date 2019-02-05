(defproject james-server "0.1.2-SNAPSHOT"
  :description "Launcher Server"
  :url "http://github.com/sulami/james-server"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/test.check "0.9.0"]]
  :repl-options {:init-ns james.core})
