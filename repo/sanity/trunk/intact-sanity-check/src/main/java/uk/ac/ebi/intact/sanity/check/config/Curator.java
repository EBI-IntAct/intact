package uk.ac.ebi.intact.sanity.check.config;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class Curator
{
    private String name;
    private String email;
    private boolean admin;

    public Curator()
    {
    }

    public Curator(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getEmail()
    {
        if (email == null) {
            return name+"@ebi.ac.uk";
        }
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public boolean isAdmin()
    {
        return admin;
    }

    public void setAdmin(boolean admin)
    {
        this.admin = admin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Curator curator = (Curator) o;

        if (name != null ? !name.equals(curator.name) : curator.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (name != null ? name.hashCode() : 0);
    }

    @Override
    public String toString() {
        return "Curator{"+getName()+" - "+getEmail()+"}";
    }
}
