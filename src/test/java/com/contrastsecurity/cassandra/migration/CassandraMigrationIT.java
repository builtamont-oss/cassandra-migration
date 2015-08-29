package com.contrastsecurity.cassandra.migration;

import com.contrastsecurity.cassandra.migration.config.MigrationType;
import com.contrastsecurity.cassandra.migration.info.MigrationInfo;
import com.contrastsecurity.cassandra.migration.info.MigrationInfoDumper;
import com.contrastsecurity.cassandra.migration.info.MigrationInfoService;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import org.junit.Test;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CassandraMigrationIT extends BaseIT {

    @Test
    public void runApiTest() {
        String[] scriptsLocations = {"migration/integ"};

        CassandraMigration cm = new CassandraMigration();
        cm.getConfigs().setScriptsLocations(scriptsLocations);
        cm.setKeyspace(getKeyspace());
        cm.migrate();

        MigrationInfoService infoService = cm.info();
        System.out.println(MigrationInfoDumper.dumpToAsciiTable(infoService.all()));
        for (MigrationInfo info : infoService.all()) {
            assertThat(info.getVersion().getVersion(), anyOf(is("1.0.0"), is("2.0.0"), is("3.0")));
            if (info.getVersion().equals("3.0")) {
                assertThat(info.getDescription(), is("Third"));
                assertThat(info.getType().name(), is(MigrationType.JAVA_DRIVER.name()));
                assertThat(info.getScript().contains(".java"), is(true));

                Select select = QueryBuilder.select()
                        .column("value")
                        .from("test1");
                select.where(eq("space", "web")).and(eq("key", "google"));
                ResultSet result = getSession().execute(select);
                assertThat(result.one().getString("value"), is("google.com"));
            } else if (info.getVersion().equals("2.0.0")) {
                assertThat(info.getDescription(), is("Second"));
                assertThat(info.getType().name(), is(MigrationType.CQL.name()));
                assertThat(info.getScript().contains(".cql"), is(true));

                Select select = QueryBuilder.select()
                        .column("title")
                        .column("message")
                        .from("contents");
                select.where(eq("id", 1));
                Row row = getSession().execute(select).one();
                assertThat(row.getString("title"), is("foo"));
                assertThat(row.getString("message"), is("bar"));
            } else if (info.getVersion().equals("1.0.0")) {
                assertThat(info.getDescription(), is("First"));
                assertThat(info.getType().name(), is(MigrationType.CQL.name()));
                assertThat(info.getScript().contains(".cql"), is(true));

                Select select = QueryBuilder.select()
                        .column("value")
                        .from("test1");
                select.where(eq("space", "foo")).and(eq("key", "bar"));
                ResultSet result = getSession().execute(select);
                assertThat(result.one().getString("value"), is("profit!"));
            }

            assertThat(info.getState().isApplied(), is(true));
            assertThat(info.getInstalledOn(), notNullValue());
        }
    }

    static boolean runCmdTestCompleted = false;
    static boolean runCmdTestSuccess = false;

    @Test
    public void runCmdTest() throws IOException, InterruptedException {
        String shell =
                "java -jar" +
                        " -Dcassandra.migration.scripts.locations=filesystem:target/test-classes/migration/integ" +
                        " -Dcassandra.migration.cluster.contactpoints=" + BaseIT.CASSANDRA_CONTACT_POINT +
                        " -Dcassandra.migration.cluster.port=" + BaseIT.CASSANDRA_PORT +
                        " -Dcassandra.migration.cluster.username=" + BaseIT.CASSANDRA_USERNAME +
                        " -Dcassandra.migration.cluster.password=" + BaseIT.CASSANDRA_PASSWORD +
                        " -Dcassandra.migration.keyspace.name=" + BaseIT.CASSANDRA__KEYSPACE +
                        " target/*-jar-with-dependencies.jar" +
                        " migrate";
        ProcessBuilder builder;
        if (isWindows()) {
            throw new NotImplementedException();
        } else {
            builder = new ProcessBuilder("bash", "-c", shell);
        }
        builder.redirectErrorStream(true);
        final Process process = builder.start();

        watch(process);

        while (!runCmdTestCompleted)
            Thread.sleep(1000L);

        assertThat(runCmdTestSuccess, is(true));
    }

    private static void watch(final Process process) {
        new Thread(new Runnable() {
            public void run() {
                BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                try {
                    while ((line = input.readLine()) != null) {
                        if (line.contains("Successfully applied 2 migrations"))
                            runCmdTestSuccess = true;
                        //System.out.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                runCmdTestCompleted = true;
            }
        }).start();
    }

    private boolean isWindows() {
        return (System.getProperty("os.name").toLowerCase()).contains("win");
    }
}