(ns com.example.server.config)

(def db-url (or (System/getenv "DATABASE_URL") 
                "jdbc:postgresql://postgres:FulcroRulez@localhost:5432/postgres"))