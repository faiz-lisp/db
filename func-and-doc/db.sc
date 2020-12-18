; --- START ---

(define (db-version) "0.27")
(define (git-url) "https://github.com/faiz-lisp/db.git")

#|
  == APIs ==
  - (def/doc (asd a) (list a))
  - (get-keys as)
  - (get-v asd)
  - (get-kv asd)
  - (get-paras asd)
  - (show)
  - (show-all)

  == Notes ==
  - 0.27 chg : db-show -> show; \nadd : git-url;
  - 0.25 inited!
  -      upd : db-ls
  -      upd : def/doc

  == TODO ==
  - (db-del k db)
  - (refr)
  - (show-keys)
  - ;.sc (scala) -> .ls
  - (switch-to-backup! [db] [file])
|#

; relies to https://github.com/faiz-lisp/libs.git
(load "g:/my/libs/chez/lib.sc") ;

; Aliases

(ali save db-save)
(ali db-ls get-keys)

; --- Syntax ---

(defsyt def/doc
  ( [_ x] (def/doc x *v) )
  ( [_ (g . args) body ...]
    (begin
      (add-to-htab! *db* `,[raw (g (lam args body ...))]) ;
      (define (g . args) body ...)
  ) )
  ( [_ x e]
    ;(begin (add-to-htab! *db* `,[raw (x e)]) ;
      (define x e)
) ) ;)

(defsyt get-v
  ( [_ key]
    (htab-value *db* 'key) )
  ( [_ key htab]
    (htab-value htab 'key)
) )

(defsyt get-kv
  ( [_ key]
    (htab-kv *db* 'key
  ) )
  ( [_ key htab]
    (htab-kv htab 'key)
) )

(defsyt get-keys
  ( [_ key-with]
    (htab-keys *db* 'key-with) )
  ( [_ key-with htab]
    (htab-keys htab 'key-with)
) )

;

(defsyt get-paras
  ( [_ key]
    (get-paras% *db* 'key) )
  ( [_ key htab]
    (get-paras% htab 'key) ;<- doc%
) )

; --- Presets ---

(setq *base-id* 1) ;1-based

(setq *types* '[func doc]) ;
(setq *defa-tab-file* "func-and-doc.dat")
(setq *db* nil  *db0* nil)

; --- pre-Impl ---

(def/va (db-init [init *db0*]) ;[db *db*] ;syt
  (setq *db* init) ;
)

(def/va (db-load [file *defa-tab-file*]) ;encrypt-with-psw?
  (if (file-exists? file)
    (load file) ;
    (db-init)
) )

; --- bef-Main ---

;(dont-return!)
(db-load)

; --- Impl ---

(def/va (add elem [ht *db*])
  (add-to-htab! ht elem) ;!? ;if funcs-with-same-names ? ;should ret T/F
)

(def/va (db-save [file *defa-tab-file*]) ;NG: only for Windows

  (sys [str `("@del /s/q " ,file ".bak 1>nul 2>nul")]) ;how about linefeed for multi-line
  (sys [str `("@ren " ,file " " ,file ".bak 2>nul")])
  
  (save-file `(set! *db* ',*db*) file) ;  
  'OK
)

;

(def/va (show-all [ht *db*])
  ht
)

(def/va (show [ht *db*])
  (map
    (lam (k)
      (cons k
        (ev `(get-paras ,k)) ;
    ) )
    (map car ht)
) )

;

(def (get-paras% ht key)
  (def (_ ht)
    (if (nilp ht) nil
      (if (eq key (caar ht))
        (let ([val (cadar ht)]) ;
          (if (consp val)
            (if [eq 'lam (car val)]
              (cadr val)
              nil )
            nil
        ) )
        [_ (cdr ht)]
  ) ) )
  (_ ht)
)

(def (q)
  (db-save)
  (exit)
)

; --- temp-Funcs/doc ---

;shell32.dll
;ShellExecute (0, GetWindowsType()==WindowsXP?"open":"runas", "cmd.exe", s, "", SW_MINIMIZE);
(def/doc (ShellExecute hwnd csOpt csFile csParas csFolder nShowFlag) "shell32.dll: can use opt: runas")

; Demos:

;(def/doc (CopyFile file-exist file-new failQ-if-exist) )

; --- Main ---

(reset-randseed)

; --- END ---