#!/usr/bin/env bb

(require
 '[babashka.curl :refer [get] :rename {get http-get}]
 '[babashka.process :as proc])
 
(def get-key clojure.core/get) 

(def security-group-id "")

(defn get-json [process]
  (json/parse-string (clojure.core/get process :out)))

(defn describe-security-group []
  (proc/shell
   {:out :string :err :string}
   "aws" "ec2" "describe-security-groups"
   "--group-ids" security-group-id))

(defn revoke-security-group [cidr]
  (let [status (proc/shell
                {:out :string :err :string}
                "aws" "ec2" "revoke-security-group-ingress"
                "--group-id" security-group-id
                "--protocol" "tcp"
                "--port" "22"
                "--cidr" cidr)]
    (cond
      (not= (get-key status :exit) 0) (do (print "Error:") (print status)))))

(defn authorize-new-security-group [cidr]
  (let [status (proc/shell
                {:out :string :err :string}
                "aws" "ec2" "authorize-security-group-ingress"
                "--group-id" security-group-id
                "--protocol" "tcp"
                "--port" "22"
                "--cidr" cidr)]
    (cond
      (not= (get-key status :exit) 0) (do (print "Error") (print status)))))


(defn get-ip-from-config [config-json] (get-in config-json ["SecurityGroups" 0 "IpPermissions" 0 "IpRanges" 0 "CidrIp"]))

(defn find-my-ip []
  (get-key (http-get "https://ifconfig.me") :body))

(defn main []
  (let
   [desc-process (describe-security-group)
    description-json (get-json desc-process)
    current-ip (find-my-ip)
    ip-from-config (get-ip-from-config description-json)]
   (if (== 0 (get desc-process :exit))
     (do
       (revoke-security-group ip-from-config)
       (authorize-new-security-group (format "%s/%d" current-ip 32)))
     (print "Description of current security group returned non-zero exit"))))
       
(main)