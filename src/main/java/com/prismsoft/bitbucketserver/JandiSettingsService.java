package com.prismsoft.bitbucketserver;

import com.atlassian.bitbucket.repository.Repository;
import javax.annotation.Nonnull;

public interface JandiSettingsService {

    @Nonnull
    JandiSettings getJandiSettings(@Nonnull Repository repository);

    @Nonnull
    JandiSettings setJandiSettings(@Nonnull Repository repository, @Nonnull JandiSettings settings);

}
