package club.oneppm.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Member.
 */
@Entity
@Table(name = "member")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Member implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "github_url")
    private String githubUrl;

    @Column(name = "other_url")
    private String otherUrl;

    @Column(name = "twitter")
    private String twitter;

    @OneToMany(mappedBy = "member")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Project> projects = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public Member username(String username) {
        this.username = username;
        return this;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public Member githubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
        return this;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getOtherUrl() {
        return otherUrl;
    }

    public Member otherUrl(String otherUrl) {
        this.otherUrl = otherUrl;
        return this;
    }

    public void setOtherUrl(String otherUrl) {
        this.otherUrl = otherUrl;
    }

    public String getTwitter() {
        return twitter;
    }

    public Member twitter(String twitter) {
        this.twitter = twitter;
        return this;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public Member projects(Set<Project> projects) {
        this.projects = projects;
        return this;
    }

    public Member addProject(Project project) {
        projects.add(project);
        project.setMember(this);
        return this;
    }

    public Member removeProject(Project project) {
        projects.remove(project);
        project.setMember(null);
        return this;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Member member = (Member) o;
        if(member.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, member.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Member{" +
            "id=" + id +
            ", username='" + username + "'" +
            ", githubUrl='" + githubUrl + "'" +
            ", otherUrl='" + otherUrl + "'" +
            ", twitter='" + twitter + "'" +
            '}';
    }
}
