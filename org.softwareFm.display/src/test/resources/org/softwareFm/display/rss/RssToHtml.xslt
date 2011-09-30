<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">


	<xsl:output method="html" />

	<xsl:template match="/">
		<html>
			<head>
				<title>
					<xsl:value-of select="rss/channel/title" />
				</title>
				<link rel="stylesheet" title="text/css" href="theme/style.css" />
			</head>
			<body>
				<div id="page-wrap">
					<div id="header-wrap">
						<div id="header">
							<div id="logo">
								<h1>
									<a>
										<xsl:attribute name="href">
											<xsl:value-of select="rss/channel/link" />
										</xsl:attribute>
										<xsl:value-of select="rss/channel/title" />
									</a>
								</h1>
							</div>
						</div>
					</div>

					<div id="content">
						<xsl:apply-templates />
					</div>

					<div id="sidebar">
						<div id="ads">
							<div class="title">
								<h3>Index</h3>
							</div>
							<div class="wrapper">
								<div class="content">
									<ul>
										<xsl:for-each select="rss/channel/item">
											<li>
												<a>
													<xsl:attribute name="href">#<xsl:number /></xsl:attribute>
													<xsl:value-of select="title" />
												</a>
											</li>
										</xsl:for-each>
									</ul>
								</div>
							</div>
						</div>

					</div>

				</div>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="text()">
		<!-- This hides all the extra junk that we don't want -->
	</xsl:template>


	<xsl:template match="item">

		<div class="post">
			<div class="title">
				<h3>
					<a>
						<xsl:attribute name="name">
							<xsl:number />
						</xsl:attribute>
						<xsl:attribute name="href">
							<xsl:value-of select="./link" />
						</xsl:attribute>
						<xsl:value-of select="./title" />
					</a>
				</h3>
			</div>
			<div class="post-wrap">
				<div class="content">
					<div class="post-content">
						<xsl:choose>
							<xsl:when test="./description">
								<xsl:value-of select="./description"  disable-output-escaping="yes" />
							</xsl:when>
							<xsl:otherwise>
								<p>
									Read it at
									<a>
										<xsl:attribute name="href">
											<xsl:value-of select="./link" />
										</xsl:attribute>
										<xsl:value-of select="./link" />
									</a>
								</p>
							</xsl:otherwise>
						</xsl:choose>
					</div>
				</div>
			</div>
		</div>

	</xsl:template>


</xsl:stylesheet>