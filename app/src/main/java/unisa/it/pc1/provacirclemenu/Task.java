package unisa.it.pc1.provacirclemenu;

import java.util.Date;

/**
 * Created by Antonio on 22/03/2018.
 */

public class Task {

    private String contenuto;
    private Date data;
    private int foto;

    public Task(String contenuto, Date data, int foto) {
        this.contenuto = contenuto;
        this.data = data;
        this.foto = foto;
    }

    public Task(String contenuto, Date data) {
        this.contenuto = contenuto;
        this.data = data;
    }

    public Task(){

    }

    public String getContenuto() {
        return contenuto;
    }

    public void setContenuto(String contenuto) {
        this.contenuto = contenuto;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public int getFoto() {
        return foto;
    }

    public void setFoto(int foto) {
        this.foto = foto;
    }
}
