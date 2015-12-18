package hudson.plugins.sauce_ondemand;

import com.saucelabs.ci.JobInformation;
import hudson.model.Action;
import hudson.plugins.sauce_ondemand.credentials.impl.SauceCredentialsImpl;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * @author Ross Rowe
 */
public abstract class AbstractAction implements Action {
    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() { return null; }

    @Override
    public String getUrlName() {
        return "sauce-ondemand-report";
    }

    public abstract List<JobInformation> getJobs();

    abstract protected SauceCredentialsImpl getCredentials();

    @SuppressWarnings("unused") // used by stapler
    public List<JobInformation> getJobsWithAuth() {
        List<JobInformation> allJobs = this.getJobs();
        for(JobInformation j: allJobs) {
            j.setHmac(getCredentials().getHMAC(j.getJobId()));
        }
        return allJobs;
    }

    /**
     *
     * @param req Standard Request Object
     * @param rsp Standard Response Object
     * @throws IOException Unable to load index.jelly template
     */
    public void doJobReport(StaplerRequest req, StaplerResponse rsp)
        throws IOException {

        SauceTestResultsById byId = new SauceTestResultsById(
            req.getParameter("jobId"),
            this.getCredentials()
        );
        try {
            req.getView(byId, "index.jelly").forward(req, rsp);
        } catch (ServletException e) {
            throw new IOException(e);
        }
    }

}
