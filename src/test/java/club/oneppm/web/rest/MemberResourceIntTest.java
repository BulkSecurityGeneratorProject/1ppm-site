package club.oneppm.web.rest;

import club.oneppm.Application;

import club.oneppm.domain.Member;
import club.oneppm.repository.MemberRepository;
import club.oneppm.service.MemberService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the MemberResource REST controller.
 *
 * @see MemberResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class MemberResourceIntTest {

    private static final String DEFAULT_USERNAME = "AAAAAAAAAA";
    private static final String UPDATED_USERNAME = "BBBBBBBBBB";

    private static final String DEFAULT_GITHUB_URL = "AAAAAAAAAA";
    private static final String UPDATED_GITHUB_URL = "BBBBBBBBBB";

    private static final String DEFAULT_OTHER_URL = "AAAAAAAAAA";
    private static final String UPDATED_OTHER_URL = "BBBBBBBBBB";

    private static final String DEFAULT_TWITTER = "AAAAAAAAAA";
    private static final String UPDATED_TWITTER = "BBBBBBBBBB";

    @Inject
    private MemberRepository memberRepository;

    @Inject
    private MemberService memberService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restMemberMockMvc;

    private Member member;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        MemberResource memberResource = new MemberResource();
        ReflectionTestUtils.setField(memberResource, "memberService", memberService);
        this.restMemberMockMvc = MockMvcBuilders.standaloneSetup(memberResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Member createEntity(EntityManager em) {
        Member member = new Member()
                .username(DEFAULT_USERNAME)
                .githubUrl(DEFAULT_GITHUB_URL)
                .otherUrl(DEFAULT_OTHER_URL)
                .twitter(DEFAULT_TWITTER);
        return member;
    }

    @Before
    public void initTest() {
        member = createEntity(em);
    }

    @Test
    @Transactional
    public void createMember() throws Exception {
        int databaseSizeBeforeCreate = memberRepository.findAll().size();

        // Create the Member

        restMemberMockMvc.perform(post("/api/members")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(member)))
                .andExpect(status().isCreated());

        // Validate the Member in the database
        List<Member> members = memberRepository.findAll();
        assertThat(members).hasSize(databaseSizeBeforeCreate + 1);
        Member testMember = members.get(members.size() - 1);
        assertThat(testMember.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testMember.getGithubUrl()).isEqualTo(DEFAULT_GITHUB_URL);
        assertThat(testMember.getOtherUrl()).isEqualTo(DEFAULT_OTHER_URL);
        assertThat(testMember.getTwitter()).isEqualTo(DEFAULT_TWITTER);
    }

    @Test
    @Transactional
    public void checkUsernameIsRequired() throws Exception {
        int databaseSizeBeforeTest = memberRepository.findAll().size();
        // set the field null
        member.setUsername(null);

        // Create the Member, which fails.

        restMemberMockMvc.perform(post("/api/members")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(member)))
                .andExpect(status().isBadRequest());

        List<Member> members = memberRepository.findAll();
        assertThat(members).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllMembers() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the members
        restMemberMockMvc.perform(get("/api/members?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(member.getId().intValue())))
                .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME.toString())))
                .andExpect(jsonPath("$.[*].githubUrl").value(hasItem(DEFAULT_GITHUB_URL.toString())))
                .andExpect(jsonPath("$.[*].otherUrl").value(hasItem(DEFAULT_OTHER_URL.toString())))
                .andExpect(jsonPath("$.[*].twitter").value(hasItem(DEFAULT_TWITTER.toString())));
    }

    @Test
    @Transactional
    public void getMember() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get the member
        restMemberMockMvc.perform(get("/api/members/{id}", member.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(member.getId().intValue()))
            .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME.toString()))
            .andExpect(jsonPath("$.githubUrl").value(DEFAULT_GITHUB_URL.toString()))
            .andExpect(jsonPath("$.otherUrl").value(DEFAULT_OTHER_URL.toString()))
            .andExpect(jsonPath("$.twitter").value(DEFAULT_TWITTER.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingMember() throws Exception {
        // Get the member
        restMemberMockMvc.perform(get("/api/members/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMember() throws Exception {
        // Initialize the database
        memberService.save(member);

        int databaseSizeBeforeUpdate = memberRepository.findAll().size();

        // Update the member
        Member updatedMember = memberRepository.findOne(member.getId());
        updatedMember
                .username(UPDATED_USERNAME)
                .githubUrl(UPDATED_GITHUB_URL)
                .otherUrl(UPDATED_OTHER_URL)
                .twitter(UPDATED_TWITTER);

        restMemberMockMvc.perform(put("/api/members")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedMember)))
                .andExpect(status().isOk());

        // Validate the Member in the database
        List<Member> members = memberRepository.findAll();
        assertThat(members).hasSize(databaseSizeBeforeUpdate);
        Member testMember = members.get(members.size() - 1);
        assertThat(testMember.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testMember.getGithubUrl()).isEqualTo(UPDATED_GITHUB_URL);
        assertThat(testMember.getOtherUrl()).isEqualTo(UPDATED_OTHER_URL);
        assertThat(testMember.getTwitter()).isEqualTo(UPDATED_TWITTER);
    }

    @Test
    @Transactional
    public void deleteMember() throws Exception {
        // Initialize the database
        memberService.save(member);

        int databaseSizeBeforeDelete = memberRepository.findAll().size();

        // Get the member
        restMemberMockMvc.perform(delete("/api/members/{id}", member.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Member> members = memberRepository.findAll();
        assertThat(members).hasSize(databaseSizeBeforeDelete - 1);
    }
}
