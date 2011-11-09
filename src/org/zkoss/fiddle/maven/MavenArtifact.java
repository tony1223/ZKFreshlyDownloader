package org.zkoss.fiddle.maven;
public class MavenArtifact {

		private String group;

		private String artifact;

		public MavenArtifact(String group, String artifat) {
			super();
			this.group = group;
			this.artifact = artifat;
		}

		public String getGroup() {
			return group;
		}

		public void setGroup(String group) {
			this.group = group;
		}

		public String getArtifact() {
			return artifact;
		}

		public void setArtifact(String artifact) {
			this.artifact = artifact;
		}

	}