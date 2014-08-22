package org.jenkinsci.plugins.builddescriptionsetter;

import static org.junit.Assert.assertEquals;
import hudson.model.FreeStyleBuild;
import hudson.model.Result;
import hudson.model.FreeStyleProject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

public class BuildDescriptionSetterTest {
	@Rule
	public JenkinsRule jenkins = new JenkinsRule();
	
	@Test
	public void shouldExpand_BUILD_NUMBER_macro() throws InterruptedException, ExecutionException, IOException {
		FreeStyleProject fooProj = jenkins.createFreeStyleProject("foo");
		fooProj.getBuildWrappersList().add(new BuildDescriptionSetter("a_#${BUILD_NUMBER}"));
		
		FreeStyleBuild fooBuild = fooProj.scheduleBuild2(0).get();
		asssertDescription(fooBuild, "a_#1");
	}

	@Test
	public void shouldExpand_JOB_NAME_full_env_macro() throws InterruptedException, ExecutionException, IOException {
		FreeStyleProject barProj = jenkins.createFreeStyleProject("bar");
		barProj.getBuildWrappersList().add(new BuildDescriptionSetter("b_${ENV,var=\"JOB_NAME\"}"));
		
		FreeStyleBuild barBuild = barProj.scheduleBuild2(0).get();
		asssertDescription(barBuild, "b_bar");
	}

	@Test
	public void shouldExpand_JOB_NAME_macro() throws InterruptedException, ExecutionException, IOException {
		FreeStyleProject barProj = jenkins.createFreeStyleProject("bar");
		barProj.getBuildWrappersList().add(new BuildDescriptionSetter("c_${JOB_NAME}"));
		
		FreeStyleBuild barBuild = barProj.scheduleBuild2(0).get();
		asssertDescription(barBuild, "c_bar");
	}

	@Test
	public void shouldExpand_JOB_NAME_macro_twice() throws InterruptedException, ExecutionException, IOException {
		FreeStyleProject barProj = jenkins.createFreeStyleProject("bar");
		barProj.getBuildWrappersList().add(new BuildDescriptionSetter("c_${JOB_NAME}_d_${JOB_NAME}"));
		
		FreeStyleBuild barBuild = barProj.scheduleBuild2(0).get();
		asssertDescription(barBuild, "c_bar_d_bar");
	}
	
	@Test
	public void shouldExpand_JOB_NAME_macro_and_JOB_NAME_full_env_macro() throws InterruptedException, ExecutionException, IOException {
		FreeStyleProject fooProj = jenkins.createFreeStyleProject("foo");
		fooProj.getBuildWrappersList().add(new BuildDescriptionSetter("d_${NODE_NAME}_${ENV,var=\"JOB_NAME\"}"));
		
		FreeStyleBuild fooBuild = fooProj.scheduleBuild2(0).get();
		asssertDescription(fooBuild, "d_master_foo");
	}

	private void asssertDescription(FreeStyleBuild build, String expectedDescription) {
		assertEquals(Result.SUCCESS, build.getResult());
		assertEquals(expectedDescription, build.getDescription());
	}
}
