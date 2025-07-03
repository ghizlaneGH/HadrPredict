import React from "react";
import { useParams } from "react-router-dom";

import DashComponent from "../components/Dash/DashComponent";

const Dashboard = () => {
  const { schoolId } = useParams();

  return (
    <div>
      <DashComponent schoolId={schoolId} />
    </div>
  );
};

export default Dashboard;
