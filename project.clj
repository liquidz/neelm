(defproject neelm "0.1.0-SNAPSHOT"
  :description "Extreme Learning Machine implementation Powered by Neanderthal"
  :url "https://github.com/liquidz/neelm"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [com.taoensso/nippy "2.13.0"]
                 [uncomplicate/neanderthal "0.18.0"]]
  :profiles
  {:dev {:source-paths  ["example"]
         :dependencies [[fudje  "0.9.7"]
                        [com.taoensso/tufte  "1.1.2"]
                        [nz.ac.waikato.cms.weka/weka-stable "3.8.0"]
                        [incanter/incanter-core "1.5.7"]
                        [incanter/incanter-charts "1.5.7"]]
         :plugins [[lein-codox "0.10.3"]]
         :repl-options {:timeout 3600000}
         :jvm-opts  ["-Xmx1000m"]}}
  :codox {:namespaces [#"^neelm\."]})
