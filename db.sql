USE [master]
GO
/****** Object:  Database [webAnalytic]    Script Date: 06.02.2022 18:11:44 ******/
CREATE DATABASE [webAnalytic]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'webAnalytic', FILENAME = N'D:\webAnalytic.mdf' , SIZE = 8192KB , MAXSIZE = UNLIMITED, FILEGROWTH = 65536KB )
 LOG ON 
( NAME = N'webAnalytic_log', FILENAME = N'D:\webAnalytic_log.ldf' , SIZE = 8192KB , MAXSIZE = 2048GB , FILEGROWTH = 65536KB )
GO
ALTER DATABASE [webAnalytic] SET COMPATIBILITY_LEVEL = 140
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [webAnalytic].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [webAnalytic] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [webAnalytic] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [webAnalytic] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [webAnalytic] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [webAnalytic] SET ARITHABORT OFF 
GO
ALTER DATABASE [webAnalytic] SET AUTO_CLOSE ON 
GO
ALTER DATABASE [webAnalytic] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [webAnalytic] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [webAnalytic] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [webAnalytic] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [webAnalytic] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [webAnalytic] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [webAnalytic] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [webAnalytic] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [webAnalytic] SET  ENABLE_BROKER 
GO
ALTER DATABASE [webAnalytic] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [webAnalytic] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [webAnalytic] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [webAnalytic] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [webAnalytic] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [webAnalytic] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [webAnalytic] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [webAnalytic] SET RECOVERY SIMPLE 
GO
ALTER DATABASE [webAnalytic] SET  MULTI_USER 
GO
ALTER DATABASE [webAnalytic] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [webAnalytic] SET DB_CHAINING OFF 
GO
ALTER DATABASE [webAnalytic] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [webAnalytic] SET TARGET_RECOVERY_TIME = 60 SECONDS 
GO
ALTER DATABASE [webAnalytic] SET DELAYED_DURABILITY = DISABLED 
GO
ALTER DATABASE [webAnalytic] SET QUERY_STORE = OFF
GO
USE [webAnalytic]
GO
/****** Object:  Schema [db_user]    Script Date: 06.02.2022 18:11:44 ******/
CREATE SCHEMA [db_user]
GO
/****** Object:  Schema [db_visitor]    Script Date: 06.02.2022 18:11:44 ******/
CREATE SCHEMA [db_visitor]
GO
/****** Object:  UserDefinedFunction [dbo].[allCountVisitedRes]    Script Date: 06.02.2022 18:11:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO


CREATE function [dbo].[allCountVisitedRes] (@fromDateTime DateTime, @toDateTime DateTime, @WebSiteID bigint)
returns bigint
as begin

	return (select count(*) as allCnt from [Visit]
	inner join [Resource] on [Resource].ID = [Visit].Resource_id
	where ([Visit].DateTime between @fromDateTime and @toDateTime) and ([Resource].WebSite_id = @WebSiteID) )
end



GO
/****** Object:  UserDefinedFunction [dbo].[allUniqueVisitor]    Script Date: 06.02.2022 18:11:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE function [dbo].[allUniqueVisitor] (@fromDateTime DateTime, @toDateTime DateTime, @WebSiteID bigint)
returns bigint
begin 
	return 
	(select count(*) from
	((select count(*) as cnt from [Visitor] 
	inner join [Visit] on [Visit].Visitor_id = [Visitor].ID
	inner join [Resource] on [Visit].Resource_id = [Resource].ID
	where ([Visit].DateTime between @fromDateTime and @toDateTime) and ([Resource].WebSite_id = @WebSiteID) group by Visitor_id) )  tmp);
end
GO
/****** Object:  UserDefinedFunction [dbo].[avgCountVisitedRes]    Script Date: 06.02.2022 18:11:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
create function [dbo].[avgCountVisitedRes] (@fromDateTime DateTime, @toDateTime DateTime, @WebSiteID bigint)
returns float 
begin

return (select AVG(cntVisit) from
(select CAST( count(*) AS FLOAT) as cntVisit from [Visit] 
	inner join [Resource] on [Visit].Resource_id = [Resource].ID
	inner join [Visitor]  on [Visit].Visitor_id  = [Visitor].ID 
	where ([Resource].WebSite_id = @WebSiteID) and
	([Visit].DateTime between @fromDateTime and @toDateTime)
	group by Visitor.ID)
as visitStat)

end
GO
/****** Object:  UserDefinedFunction [dbo].[newVisitorCount]    Script Date: 06.02.2022 18:11:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE function [dbo].[newVisitorCount] (@fromDateTime DateTime, @toDateTime DateTime, @WebSiteID bigint)
returns bigint 
begin
	return (select count(*) from Visit
			inner join Resource on Resource.ID = Visit.Resource_id
			inner join Visitor on Visitor.ID = Visit.Visitor_id
			where
			([Resource].WebSite_id = @WebSiteID) and
			([Visitor].dateReg between @fromDateTime and @toDateTime))

end
GO
/****** Object:  Table [dbo].[Resource]    Script Date: 06.02.2022 18:11:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Resource](
	[ID] [bigint] IDENTITY(1,1) NOT NULL,
	[Page] [nvarchar](1024) NULL,
	[WebSite_id] [bigint] NOT NULL,
 CONSTRAINT [PK_TargetResource] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [IX_Resource] UNIQUE NONCLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[browser]    Script Date: 06.02.2022 18:11:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[browser](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[name] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK_browser] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Visit]    Script Date: 06.02.2022 18:11:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Visit](
	[ID] [bigint] IDENTITY(1,1) NOT NULL,
	[Resource_id] [bigint] NOT NULL,
	[Visitor_id] [bigint] NOT NULL,
	[DateTime] [datetime] NOT NULL,
	[referer_id] [bigint] NULL,
 CONSTRAINT [PK_Visit] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Visitor]    Script Date: 06.02.2022 18:11:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Visitor](
	[ID] [bigint] IDENTITY(1,1) NOT NULL,
	[dateReg] [datetime] NOT NULL,
	[country_id] [bigint] NOT NULL,
	[browser_id] [bigint] NOT NULL,
	[device_id] [bigint] NOT NULL,
	[os_id] [bigint] NOT NULL,
	[ScResolution_id] [bigint] NOT NULL,
 CONSTRAINT [PK_User] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  UserDefinedFunction [dbo].[statBrowser]    Script Date: 06.02.2022 18:11:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE function [dbo].[statBrowser] (@fromDateTime DateTime, @toDateTime DateTime, @WebSiteID bigint)
returns table as 
return
select Browser, count(*) as cnt from  
(select Browser.name as Browser from Visit
	join Resource on Resource.ID = Visit.Resource_id
	join Visitor on Visitor.ID = Visit.Visitor_id
	join Browser on Browser.id = Visitor.browser_id
	where (WebSite_id = @WebSiteID) and
	([Visit].DateTime between @fromDateTime and @toDateTime)
	group by Visitor.id, Browser.name) t
	group by Browser


GO
/****** Object:  Table [dbo].[OS]    Script Date: 06.02.2022 18:11:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OS](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[name] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK_OS] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  UserDefinedFunction [dbo].[statOs]    Script Date: 06.02.2022 18:11:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE function [dbo].[statOs] (@fromDateTime DateTime, @toDateTime DateTime, @WebSiteID bigint)
returns table as 
return
select OS, count(*) as cnt from  
(select OS.name as OS from Visit
	join Resource on Resource.ID = Visit.Resource_id
	join Visitor on Visitor.ID = Visit.Visitor_id
	join OS on OS.id = Visitor.os_id
	where (WebSite_id = @WebSiteID) and
	([Visit].DateTime between @fromDateTime and @toDateTime)
	group by Visitor.id, OS.name) t
	group by OS
GO
/****** Object:  Table [dbo].[Device]    Script Date: 06.02.2022 18:11:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Device](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[name] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK_Device] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  UserDefinedFunction [dbo].[statDevice]    Script Date: 06.02.2022 18:11:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE function [dbo].[statDevice] (@fromDateTime DateTime, @toDateTime DateTime, @WebSiteID bigint)
returns table as 
return
select Device, count(*) as cnt from  
(select Device.name as Device from Visit
	join Resource on Resource.ID = Visit.Resource_id
	join Visitor on Visitor.ID = Visit.Visitor_id
	join Device on Device.id = Visitor.device_id
	where (WebSite_id = @WebSiteID) and
	([Visit].DateTime between @fromDateTime and @toDateTime)
	group by Visitor.id, Device.name) t
	group by Device
GO
/****** Object:  Table [dbo].[ScResolution]    Script Date: 06.02.2022 18:11:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ScResolution](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[value] [nvarchar](9) NOT NULL,
 CONSTRAINT [PK_ScResolution] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  UserDefinedFunction [dbo].[statScResolution]    Script Date: 06.02.2022 18:11:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE function [dbo].[statScResolution] (@fromDateTime DateTime, @toDateTime DateTime, @WebSiteID bigint)
returns table as 
return
select ScResolution, count(*) as cnt from  
(select ScResolution.value as ScResolution from Visit
	join Resource on Resource.ID = Visit.Resource_id
	join Visitor on Visitor.ID = Visit.Visitor_id
	join ScResolution on ScResolution.id = Visitor.ScResolution_id
	where (WebSite_id = @WebSiteID) and
	([Visit].DateTime between @fromDateTime and @toDateTime)
	group by Visitor.id, ScResolution.value) t
	group by ScResolution
GO
/****** Object:  UserDefinedFunction [dbo].[statResBrowser]    Script Date: 06.02.2022 18:11:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE function [dbo].[statResBrowser] (@fromDateTime DateTime, @toDateTime DateTime, @WebSiteID bigint)
returns table as 
return
		select Browser, page, count(*) as cnt from  
(select Browser.name as Browser, page from Visit
	join Resource on Resource.ID = Visit.Resource_id
	join Visitor on Visitor.ID = Visit.Visitor_id
	join Browser on Browser.id = Visitor.browser_id
	where ([Resource].WebSite_id = @WebSiteID) and
	([Visit].DateTime between @fromDateTime and @toDateTime)
	group by Visitor.id, Browser.name, page) t
	group by Browser, page
GO
/****** Object:  UserDefinedFunction [dbo].[statResOS]    Script Date: 06.02.2022 18:11:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE function [dbo].[statResOS] (@fromDateTime DateTime, @toDateTime DateTime, @WebSiteID bigint)
returns table as 
return
		select page, OS, count(*) as cnt from 
(select OS.name as os, page from Visit
	join Resource on Resource.ID = Visit.Resource_id
	join Visitor on Visitor.ID = Visit.Visitor_id
	join OS on OS.id = Visitor.os_id
	where ([Resource].WebSite_id = @WebSiteID) and
	([Visit].DateTime between @fromDateTime and @toDateTime)
	group by Visitor.id, page, OS.name) t
	group by page, OS
GO
/****** Object:  UserDefinedFunction [dbo].[statResDevice]    Script Date: 06.02.2022 18:11:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE function [dbo].[statResDevice] (@fromDateTime DateTime, @toDateTime DateTime, @WebSiteID bigint)
returns table as 
return
		select page, Device, count(*) as cnt from 
(select Device.name as Device, page from Visit
	join Resource on Resource.ID = Visit.Resource_id
	join Visitor on Visitor.ID = Visit.Visitor_id
	join Device on Device.id = Visitor.device_id
	where ([Resource].WebSite_id = @WebSiteID) and
	([Visit].DateTime between @fromDateTime and @toDateTime)
	group by Visitor.id, page, Device.name) t
	group by page, Device
GO
/****** Object:  UserDefinedFunction [dbo].[statResScResolution]    Script Date: 06.02.2022 18:11:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE function [dbo].[statResScResolution] (@fromDateTime DateTime, @toDateTime DateTime, @WebSiteID bigint)
returns table as 
return
		select page, ScResolution, count(*) as cnt from 
(select ScResolution.value as ScResolution, page from Visit
	join Resource on Resource.ID = Visit.Resource_id
	join Visitor on Visitor.ID = Visit.Visitor_id
	join ScResolution on ScResolution.id = Visitor.ScResolution_id
	where ([Resource].WebSite_id = @WebSiteID) and
	([Visit].DateTime between @fromDateTime and @toDateTime)
	group by Visitor.id, page, ScResolution.value) t
	group by page, ScResolution
GO
/****** Object:  Table [dbo].[referer]    Script Date: 06.02.2022 18:11:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[referer](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[host] [nvarchar](255) NULL,
 CONSTRAINT [PK_referer] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  UserDefinedFunction [dbo].[statRefBrowser]    Script Date: 06.02.2022 18:11:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO


CREATE function [dbo].[statRefBrowser] (@fromDateTime DateTime, @toDateTime DateTime, @WebSiteID bigint)
returns table as 
return
select Browser, referer, count(*) as cnt from  
(select Browser.name as Browser, Referer.host as referer from Visit
	join Resource on Resource.ID = Visit.Resource_id
	join Visitor on Visitor.ID = Visit.Visitor_id
	join Browser on Browser.id = Visitor.browser_id
	join referer on Referer.id = Visit.referer_id
	where ([Resource].WebSite_id = @WebSiteID) and
	([Visit].DateTime between @fromDateTime and @toDateTime)
	group by Visitor.id, Browser.name,Referer.host) t
	group by Browser, Referer
GO
/****** Object:  UserDefinedFunction [dbo].[statRefDevice]    Script Date: 06.02.2022 18:11:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO


CREATE function [dbo].[statRefDevice] (@fromDateTime DateTime, @toDateTime DateTime, @WebSiteID bigint)
returns table as 
return
select referer, Device, count(*) as cnt from 
(select Device.name as Device, Referer.host as referer from Visit
	join Resource on Resource.ID = Visit.Resource_id
	join Visitor on Visitor.ID = Visit.Visitor_id
	join Device on Device.id = Visitor.device_id
	join referer on Referer.id = Visit.referer_id
	where ([Resource].WebSite_id = @WebSiteID) and
	([Visit].DateTime between @fromDateTime and @toDateTime)
	group by Visitor.id, Referer.host, Device.name) t
	group by Referer, Device
GO
/****** Object:  UserDefinedFunction [dbo].[statRefOS]    Script Date: 06.02.2022 18:11:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO


CREATE function [dbo].[statRefOS] (@fromDateTime DateTime, @toDateTime DateTime, @WebSiteID bigint)
returns table as 
return
select referer, OS, count(*) as cnt from 
(select OS.name as os, Referer.host as referer from Visit
	join Resource on Resource.ID = Visit.Resource_id
	join Visitor on Visitor.ID = Visit.Visitor_id
	join OS on OS.id = Visitor.os_id
	join referer on Referer.id = Visit.referer_id
	where ([Resource].WebSite_id = @WebSiteID) and
	([Visit].DateTime between @fromDateTime and @toDateTime)
	group by Visitor.id, Referer.host, OS.name) t
	group by Referer, OS
GO
/****** Object:  UserDefinedFunction [dbo].[statRefScResolution]    Script Date: 06.02.2022 18:11:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO


CREATE function [dbo].[statRefScResolution] (@fromDateTime DateTime, @toDateTime DateTime, @WebSiteID bigint)
returns table as 
return
select referer, ScResolution, count(*) as cnt from 
(select ScResolution.value as ScResolution, Referer.host as referer from Visit
	join Resource on Resource.ID = Visit.Resource_id
	join Visitor on Visitor.ID = Visit.Visitor_id
	join ScResolution on ScResolution.id = Visitor.ScResolution_id
	join referer on Referer.id = Visit.referer_id
	where ([Resource].WebSite_id = @WebSiteID) and
	([Visit].DateTime between @fromDateTime and @toDateTime)
	group by Visitor.id, Referer.host, ScResolution.value) t
	group by Referer, ScResolution
GO
/****** Object:  Table [dbo].[country]    Script Date: 06.02.2022 18:11:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[country](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[iso_code] [nvarchar](4) NOT NULL,
 CONSTRAINT [PK_country] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  UserDefinedFunction [dbo].[statCountry]    Script Date: 06.02.2022 18:11:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE function [dbo].[statCountry] (@fromDateTime DateTime, @toDateTime DateTime, @WebSiteID bigint)
returns table as 
return
select Country, count(*) as cnt from  
(select Country.iso_code as Country from Visit
	join Resource on Resource.ID = Visit.Resource_id
	join Visitor on Visitor.ID = Visit.Visitor_id
	join Country on Country.id = Visitor.country_id
	where (WebSite_id = @WebSiteID) and
	([Visit].DateTime between @fromDateTime and @toDateTime)
	group by Visitor.id, Country.iso_code) t
	group by Country


GO
/****** Object:  UserDefinedFunction [dbo].[statResCountry]    Script Date: 06.02.2022 18:11:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO


CREATE function [dbo].[statResCountry] (@fromDateTime DateTime, @toDateTime DateTime, @WebSiteID bigint)
returns table as 
return
		select page , Country, count(*) as cnt from  
(select Country.iso_code as Country, page from Visit
	join Resource on Resource.ID = Visit.Resource_id
	join Visitor on Visitor.ID = Visit.Visitor_id
	join Country on Country.id = Visitor.country_id
	where ([Resource].WebSite_id = @WebSiteID) and
	([Visit].DateTime between @fromDateTime and @toDateTime)
	group by Visitor.id, Country.iso_code,page ) t
	group by Country, page 
GO
/****** Object:  UserDefinedFunction [dbo].[statRefCountry]    Script Date: 06.02.2022 18:11:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO



CREATE function [dbo].[statRefCountry] (@fromDateTime DateTime, @toDateTime DateTime, @WebSiteID bigint)
returns table as 
return
select Referer, Country, count(*) as cnt from  
(select Country.iso_code as Country, Referer.host as referer from Visit
	join Resource on Resource.ID = Visit.Resource_id
	join Visitor on Visitor.ID = Visit.Visitor_id
	join Country on Country.id = Visitor.country_id
	join referer on Referer.id = Visit.referer_id
	where ([Resource].WebSite_id = @WebSiteID) and
	([Visit].DateTime between @fromDateTime and @toDateTime)
	group by Visitor.id, Country.iso_code,Referer.host) t
	group by Country, Referer


GO
/****** Object:  UserDefinedFunction [dbo].[dateRange]    Script Date: 06.02.2022 18:11:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[dateRange](@WebSiteID bigint)
RETURNS TABLE 
AS
RETURN 
(
	select MIN([datetime]) AS MIN, MAX([datetime]) AS MAX from Visit
	inner join Resource on Resource.ID = Visit.Resource_id
	inner join Visitor on Visitor.ID = Visit.Visitor_id
	where Resource.WebSite_id = @WebSiteID
)
GO
/****** Object:  UserDefinedFunction [dbo].[statVisitOnDay]    Script Date: 06.02.2022 18:11:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

create function [dbo].[statVisitOnDay] (@fromDateTime DateTime, @toDateTime DateTime, @WebSiteID bigint)
returns table as 
return select CAST([DateTime] as DATE) as day, count(*) as cnt from Visit
inner join [Resource] on [Resource].ID = [Visit].Resource_id
where 
([Resource].WebSite_id = @WebSiteID) and
	([Visit].DateTime between @fromDateTime and @toDateTime)
group by CAST([DateTime] as DATE);
GO
/****** Object:  UserDefinedFunction [dbo].[statResource]    Script Date: 06.02.2022 18:11:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
create function [dbo].[statResource] (@fromDateTime DateTime, @toDateTime DateTime, @WebSiteID bigint)
returns table as
return 
	select [Resource].Page as page, count(*) as cnt from [Visit]
	inner join [Resource] on [Resource].ID = [Visit].Resource_id
	where ([Visit].DateTime between @fromDateTime and @toDateTime) and ([Resource].WebSite_id = @WebSiteID)
	group by [Resource].Page;
GO
/****** Object:  UserDefinedFunction [dbo].[statReferer]    Script Date: 06.02.2022 18:11:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE function [dbo].[statReferer] (@fromDateTime DateTime, @toDateTime DateTime, @WebSiteID bigint)
returns table as
return 
	select referer.host as referer, count(*) as cnt from [Visit]
	join [Resource] on [Resource].ID = [Visit].Resource_id
	join referer on referer.id = Visit.referer_id
	where (([Visit].DateTime between @fromDateTime and @toDateTime) and ([Resource].WebSite_id = @WebSiteID))
	group by referer.host;

GO
/****** Object:  Table [dbo].[AccessUserWebSite]    Script Date: 06.02.2022 18:11:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[AccessUserWebSite](
	[WebSite_id] [bigint] NOT NULL,
	[User_id] [bigint] NOT NULL,
 CONSTRAINT [PK_AccessUserWebSite] PRIMARY KEY CLUSTERED 
(
	[WebSite_id] ASC,
	[User_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Report]    Script Date: 06.02.2022 18:11:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Report](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[User_id] [bigint] NOT NULL,
	[FileName] [nvarchar](100) NOT NULL,
	[Source] [varbinary](max) NULL,
 CONSTRAINT [PK_Report] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[User]    Script Date: 06.02.2022 18:11:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[User](
	[ID] [bigint] IDENTITY(1,1) NOT NULL,
	[Login] [nvarchar](50) NOT NULL,
	[Password] [binary](32) NOT NULL,
	[Name] [nvarchar](50) NOT NULL,
	[isAdmin] [bit] NULL,
	[UserAdmin_id] [bigint] NULL,
 CONSTRAINT [PK__Account__3214EC2778FC28F9] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [uniq_login] UNIQUE NONCLUSTERED 
(
	[Login] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[WebSite]    Script Date: 06.02.2022 18:11:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[WebSite](
	[ID] [bigint] IDENTITY(1,1) NOT NULL,
	[Domain] [nvarchar](255) NOT NULL,
	[Admin_id] [bigint] NOT NULL,
	[DateTime] [datetime] NOT NULL,
 CONSTRAINT [PK_Target] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [FileName unique]    Script Date: 06.02.2022 18:11:44 ******/
CREATE UNIQUE NONCLUSTERED INDEX [FileName unique] ON [dbo].[Report]
(
	[FileName] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
/****** Object:  Index [IX_Report]    Script Date: 06.02.2022 18:11:44 ******/
CREATE NONCLUSTERED INDEX [IX_Report] ON [dbo].[Report]
(
	[User_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
/****** Object:  Index [IX_Resource_1]    Script Date: 06.02.2022 18:11:44 ******/
CREATE NONCLUSTERED INDEX [IX_Resource_1] ON [dbo].[Resource]
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [IX_User]    Script Date: 06.02.2022 18:11:44 ******/
CREATE NONCLUSTERED INDEX [IX_User] ON [dbo].[User]
(
	[Login] ASC,
	[Name] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
/****** Object:  Index [IX_Visit]    Script Date: 06.02.2022 18:11:44 ******/
CREATE NONCLUSTERED INDEX [IX_Visit] ON [dbo].[Visit]
(
	[Resource_id] ASC,
	[Visitor_id] ASC,
	[DateTime] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
/****** Object:  Index [IX_Visitor]    Script Date: 06.02.2022 18:11:44 ******/
CREATE NONCLUSTERED INDEX [IX_Visitor] ON [dbo].[Visitor]
(
	[dateReg] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [IX_WebSite]    Script Date: 06.02.2022 18:11:44 ******/
CREATE NONCLUSTERED INDEX [IX_WebSite] ON [dbo].[WebSite]
(
	[Domain] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
ALTER TABLE [dbo].[Visit] ADD  CONSTRAINT [DF_Visit_DateTime]  DEFAULT (getdate()) FOR [DateTime]
GO
ALTER TABLE [dbo].[Visitor] ADD  CONSTRAINT [DF_Visitor_dateReg]  DEFAULT (getdate()) FOR [dateReg]
GO
ALTER TABLE [dbo].[WebSite] ADD  CONSTRAINT [DF_WebSite_DateTime]  DEFAULT (getdate()) FOR [DateTime]
GO
ALTER TABLE [dbo].[AccessUserWebSite]  WITH CHECK ADD  CONSTRAINT [FK_AccessUserWebSite_User] FOREIGN KEY([User_id])
REFERENCES [dbo].[User] ([ID])
GO
ALTER TABLE [dbo].[AccessUserWebSite] CHECK CONSTRAINT [FK_AccessUserWebSite_User]
GO
ALTER TABLE [dbo].[AccessUserWebSite]  WITH CHECK ADD  CONSTRAINT [FK_AccessUserWebSite_WebSite] FOREIGN KEY([WebSite_id])
REFERENCES [dbo].[WebSite] ([ID])
ON UPDATE CASCADE
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[AccessUserWebSite] CHECK CONSTRAINT [FK_AccessUserWebSite_WebSite]
GO
ALTER TABLE [dbo].[Report]  WITH CHECK ADD  CONSTRAINT [FK_Report_User] FOREIGN KEY([User_id])
REFERENCES [dbo].[User] ([ID])
ON UPDATE CASCADE
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[Report] CHECK CONSTRAINT [FK_Report_User]
GO
ALTER TABLE [dbo].[Resource]  WITH CHECK ADD  CONSTRAINT [FK_TargetResource_TargetDomain] FOREIGN KEY([WebSite_id])
REFERENCES [dbo].[WebSite] ([ID])
ON UPDATE CASCADE
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[Resource] CHECK CONSTRAINT [FK_TargetResource_TargetDomain]
GO
ALTER TABLE [dbo].[User]  WITH CHECK ADD  CONSTRAINT [FK_User_User] FOREIGN KEY([UserAdmin_id])
REFERENCES [dbo].[User] ([ID])
GO
ALTER TABLE [dbo].[User] CHECK CONSTRAINT [FK_User_User]
GO
ALTER TABLE [dbo].[Visit]  WITH CHECK ADD  CONSTRAINT [FK_Visit_referer] FOREIGN KEY([referer_id])
REFERENCES [dbo].[referer] ([id])
GO
ALTER TABLE [dbo].[Visit] CHECK CONSTRAINT [FK_Visit_referer]
GO
ALTER TABLE [dbo].[Visit]  WITH CHECK ADD  CONSTRAINT [FK_Visit_TargetResource] FOREIGN KEY([Resource_id])
REFERENCES [dbo].[Resource] ([ID])
ON UPDATE CASCADE
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[Visit] CHECK CONSTRAINT [FK_Visit_TargetResource]
GO
ALTER TABLE [dbo].[Visit]  WITH CHECK ADD  CONSTRAINT [FK_Visit_Visitor] FOREIGN KEY([Visitor_id])
REFERENCES [dbo].[Visitor] ([ID])
ON UPDATE CASCADE
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[Visit] CHECK CONSTRAINT [FK_Visit_Visitor]
GO
ALTER TABLE [dbo].[Visitor]  WITH CHECK ADD  CONSTRAINT [FK_Visitor_browser] FOREIGN KEY([browser_id])
REFERENCES [dbo].[browser] ([id])
GO
ALTER TABLE [dbo].[Visitor] CHECK CONSTRAINT [FK_Visitor_browser]
GO
ALTER TABLE [dbo].[Visitor]  WITH CHECK ADD  CONSTRAINT [FK_Visitor_country] FOREIGN KEY([country_id])
REFERENCES [dbo].[country] ([id])
GO
ALTER TABLE [dbo].[Visitor] CHECK CONSTRAINT [FK_Visitor_country]
GO
ALTER TABLE [dbo].[Visitor]  WITH CHECK ADD  CONSTRAINT [FK_Visitor_Device] FOREIGN KEY([device_id])
REFERENCES [dbo].[Device] ([id])
GO
ALTER TABLE [dbo].[Visitor] CHECK CONSTRAINT [FK_Visitor_Device]
GO
ALTER TABLE [dbo].[Visitor]  WITH CHECK ADD  CONSTRAINT [FK_Visitor_OS] FOREIGN KEY([os_id])
REFERENCES [dbo].[OS] ([id])
GO
ALTER TABLE [dbo].[Visitor] CHECK CONSTRAINT [FK_Visitor_OS]
GO
ALTER TABLE [dbo].[Visitor]  WITH CHECK ADD  CONSTRAINT [FK_Visitor_ScResolution] FOREIGN KEY([ScResolution_id])
REFERENCES [dbo].[ScResolution] ([id])
GO
ALTER TABLE [dbo].[Visitor] CHECK CONSTRAINT [FK_Visitor_ScResolution]
GO
ALTER TABLE [dbo].[WebSite]  WITH CHECK ADD  CONSTRAINT [FK_TargetDomain_User] FOREIGN KEY([Admin_id])
REFERENCES [dbo].[User] ([ID])
ON UPDATE CASCADE
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[WebSite] CHECK CONSTRAINT [FK_TargetDomain_User]
GO
/****** Object:  StoredProcedure [dbo].[CreateVisit]    Script Date: 06.02.2022 18:11:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[CreateVisit] 
@Visitor_id bigint,
@res_id bigint,
@Referer_host nvarchar(1024)
AS
BEGIN

-- Referer
declare @Referer_id bigint = (select id from Referer where referer.host = @Referer_host)
if (@Referer_host is null)
begin
	insert into referer ([host]) values (@Referer_host);
	set @Referer_id = (select scope_identity());
end

insert into [Visit] (Visitor_id, Resource_id, Referer_id) values (@Visitor_id, @res_id, @Referer_id);
END
GO
/****** Object:  StoredProcedure [dbo].[CreateVisitor]    Script Date: 06.02.2022 18:11:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[CreateVisitor] 
@Country nvarchar(4),
@Browser nvarchar(50),
@OS nvarchar(50),
@ScResolution nvarchar(50),
@Device nvarchar(50)
AS
BEGIN

-- country
declare @Country_id bigint = (select id from country where country.iso_code = @Country)
if (@Country_id is null)
begin
	insert into country (iso_code) values (@Country);
	set @Country_id = (select scope_identity());
end

-- browser
declare @Browser_id bigint = (select id from browser where browser.[name] = @Browser)
if (@Browser_id is null)
begin
	insert into browser ([name]) values (@Browser);
	set @Browser_id = (select scope_identity());
end

-- OS
declare @OS_id bigint = (select id from OS where OS.name = @OS)
if (@OS_id is null)
begin
	insert into OS ([name]) values (@OS);
	set @OS_id = (select scope_identity());
end

-- Device
declare @Device_id bigint = (select id from Device where Device.name = @Device)
if (@Device_id is null)
begin
	insert into Device ([name]) values (@Device);
	set @Device_id = (select scope_identity());
end

-- ScResolution
declare @ScResolution_id bigint = (select id from ScResolution where ScResolution.value = @ScResolution)
if (@ScResolution_id is null)
begin
	insert into ScResolution ([value]) values (@ScResolution);
	set @ScResolution_id = (select scope_identity());
end

insert into [Visitor] (Country_id, Browser_id, OS_id, Device_id, ScResolution_id) values (@Country_id, @Browser_id, @OS_id, @Device_id, @ScResolution_id);
END
GO
USE [master]
GO
ALTER DATABASE [webAnalytic] SET  READ_WRITE 
GO
