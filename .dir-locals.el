((java-mode
  . ((eglot-workspace-configuration
      . (lambda (server)
	  `(:java
	   (:format
            (:enabled t
		      :settings
		      (:url ,(concat "file://" (expand-file-name "format.xml"))
			    :profile "Default")))))))))
