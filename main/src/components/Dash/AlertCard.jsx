import React from "react";
import { Link } from "react-router-dom";

export default function AlertCard({ schoolId }) {
  return (
    <Link to={schoolId ? `/alertes/${schoolId}` : "/alertes/global"}>
      <div>
        <h3>Alertes</h3>
      </div>
    </Link>
  );
}
