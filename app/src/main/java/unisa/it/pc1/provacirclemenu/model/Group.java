package unisa.it.pc1.provacirclemenu.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Group extends Chatter implements Serializable  {

    private String group_id;
    private String nome;
    private String immagine;
    private String thumb_image;
    private ArrayList<User> utenti;

    public Group() {
    }

    public Group(String group_id, String nome, String immagine, ArrayList<User> utenti, String thumb_image) {
        this.group_id = group_id;
        this.nome = nome;
        this.immagine = immagine;
        this.utenti = utenti;
        this.thumb_image = thumb_image;
    }

    public Group(String group_id, String nome, String immagine, ArrayList<User> utenti) {
        this.group_id = group_id;
        this.nome = nome;
        this.immagine = immagine;
        this.utenti = utenti;
    }

    public Group(String group_id, String nome, String immagine,String thumb_image) {
        this.group_id = group_id;
        this.nome = nome;
        this.immagine = immagine;
        this.thumb_image = thumb_image;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getImmagine() {
        return immagine;
    }

    public void setImmagine(String immagine) {
        this.immagine = immagine;
    }

    public ArrayList<User> getUtenti() {
        return utenti;
    }

    public void setUtenti(ArrayList<User> utenti) {
        this.utenti = utenti;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }
}
