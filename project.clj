(defproject neelm "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-RC2"]
                 [uncomplicate/neanderthal "0.17.1"]]
  :profiles
  {:dev {:source-paths  ["example"]
         :dependencies [[incanter/incanter-core "1.5.7"]
                        [incanter/incanter-charts "1.5.7"]]}})
