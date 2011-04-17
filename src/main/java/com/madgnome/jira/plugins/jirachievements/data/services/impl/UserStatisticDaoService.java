package com.madgnome.jira.plugins.jirachievements.data.services.impl;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.madgnome.jira.plugins.jirachievements.data.ao.StatisticRef;
import com.madgnome.jira.plugins.jirachievements.data.ao.UserStatistic;
import com.madgnome.jira.plugins.jirachievements.data.ao.UserWrapper;
import com.madgnome.jira.plugins.jirachievements.data.services.IUserStatisticDaoService;

public class UserStatisticDaoService extends BaseDaoService<UserStatistic> implements IUserStatisticDaoService
{
  @Override
  protected Class<UserStatistic> getClazz()
  {
    return UserStatistic.class;
  }

  public UserStatisticDaoService(ActiveObjects ao)
  {
    super(ao);
  }

  @Override
  public UserStatistic getStatistic(UserWrapper userWrapper, String ref)
  {
    StatisticRef[] statisticRefs = ao.find(StatisticRef.class, "REF = ?", ref);
    if (statisticRefs == null)
    {
      logger.info("Statistic with ref {} doesn't exist", ref);
      return null;
    }

    return getOrCreate(statisticRefs[0], userWrapper);
  }

  private UserStatistic getOrCreate(StatisticRef statisticRef, UserWrapper userWrapper)
  {
    final UserStatistic[] userStatistics = ao.find(UserStatistic.class, "STATISTIC_REF_ID = ? AND USER_WRAPPER_ID = ?", statisticRef.getID(), userWrapper.getID());
    if (userStatistics.length > 1)
    {
      throw new IllegalStateException("Found more than one statistic (" + userStatistics.length + ") with ref " + statisticRef.getRef() + " for user " + userWrapper.getJiraUserName());
    }

    return userStatistics.length == 0 ? create(statisticRef, userWrapper) : userStatistics[0];
  }

  private UserStatistic create(StatisticRef statisticRef, UserWrapper userWrapper)
  {
    UserStatistic userStatistic = ao.create(UserStatistic.class);
    userStatistic.setStatisticRef(statisticRef);
    userStatistic.setUserWrapper(userWrapper);
    userStatistic.save();

    return userStatistic;
  }


}
